package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx._
import org.apache.mahout.math.{Matrix, RandomAccessSparseVector, DenseVector, SparseMatrix}
import java.util.Date
import org.apache.mahout.math.solver.LSMR
import com.fijimf.deepfij.util.DateStream
import org.apache.log4j.Logger
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.special.Erf
import com.fijimf.deepfij.statx.ModelContext
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.statx.models.linreg.LSMRSolver

class NaiveLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "naive-linear-regression"

  val statW: MetaStat = new MetaStat(modelName = name, modelKey = key, statKey = "win-predictor", name = "Win Predictor", format = "%9.5f", higherIsBetter = true)
  val statP: MetaStat = new MetaStat(modelName = name, modelKey = key, statKey = "point-predictor", name = "Point Predictor", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statW, statP)

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
        (pair._2, teamMap(pair._1.awayTeam.key)) -> -1.0
      )
    }).flatten.toMap

    val b0 = games.map(g => (g.result.homeScore - g.result.awayScore).toDouble)
    val b1 = games.map(g => math.signum(g.result.homeScore - g.result.awayScore).toDouble)

    val x0 = LSMRSolver.solve(A,games.size, teamMap.size,  b0)
    val x1 = LSMRSolver.solve(A,games.size, teamMap.size,  b1)


    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statW, date, s.teamByKey(pair._1), x0(pair._2)).
        update(statP, date, s.teamByKey(pair._1), x1(pair._2))
    })

  }
}








