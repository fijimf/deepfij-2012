package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx._
import org.apache.log4j.Logger
import java.util.Date
import com.fijimf.deepfij.util.DateStream
import org.apache.mahout.math.{DenseVector, SparseMatrix, RandomAccessSparseVector}
import org.apache.mahout.math.solver.LSMR
import com.fijimf.deepfij.statx.ModelContext
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.statx.models.linreg.LSMRSolver

class HomeAdjustedLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "homadj-linear-regression"

  val statHAPP: MetaStat = new MetaStat(statKey = "homadj-point-predictor", name = "Home Adj Point Predictor", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statHAPP)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap

    val A = games.zipWithIndex.map(pair => {
      List(
        (pair._2, teamMap(pair._1.homeTeam.key)) -> 1.0,
        (pair._2, teamMap(pair._1.awayTeam.key)) -> -1.0,
        (pair._2, teamMap.size) -> 1.0
      )
    }).flatten.toMap

    val b = games.map(g => (g.result.homeScore - g.result.awayScore).toDouble)

    val x = LSMRSolver.solve(A, games.size, teamMap.size + 1, b)

    val homeAdv = x(teamMap.size)

    teamMap.foldLeft(ctx)((ctx, p) => {
      val (teamKey, teamIndex) = p
      val t: Team = s.teamByKey(teamKey)
      ctx.update(statHAPP, date, t, x(teamIndex))
    }).updateParm(statHAPP, date, "homeAdv", homeAdv)
  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble

}
