package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx._
import com.fijimf.deepfij.modelx.{Game, Schedule, MetaStat, Team}
import org.apache.log4j.Logger
import java.util.Date
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.statx.models.linreg.LSMRSolver
import org.apache.commons.lang.time.DateUtils
import com.fijimf.deepfij.statx.ModelContext
import com.fijimf.deepfij.util.DateStream
import scala.Some

class OffenseDefenseLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Offense-Defense Linear Regression Model"

  def key = "off-def-linear-regression"

  val statOff: MetaStat = new MetaStat(statKey = "off-point-predictor", name = "Home Point Predictor", format = "%9.5f", higherIsBetter = true)
  val statDef: MetaStat = new MetaStat(statKey = "def-point-predictor", name = "Away Point Predictor", format = "%9.5f", higherIsBetter = false)

  def statistics = List(statOff, statDef)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap



    val A = games.zipWithIndex.map(pair => {
      val homeScoreRowIndex: Int = pair._2
      val awayScoreRowIndex: Int = games.size + homeScoreRowIndex

      val homeKey: String = pair._1.homeTeam.key
      val awayKey: String = pair._1.awayTeam.key
      val homeOffenseColIndex: Int = teamMap(homeKey)
      val homeDefenseColIndex: Int = homeOffenseColIndex + teamMap.size
      val awayOffenseColIndex: Int = teamMap(awayKey)
      val awayDefenseColIndex: Int = awayOffenseColIndex + teamMap.size
      val scoreBaselineColIndex: Int = 2 * teamMap.size
      List(
        (homeScoreRowIndex, homeOffenseColIndex) -> 1.0,
        (homeScoreRowIndex, awayDefenseColIndex) -> 1.0,
        (homeScoreRowIndex, scoreBaselineColIndex) -> 1.0,
        (awayScoreRowIndex, awayOffenseColIndex) -> 1.0,
        (awayScoreRowIndex, homeDefenseColIndex) -> 1.0,
        (awayScoreRowIndex, scoreBaselineColIndex) -> 1.0
      )
    }).flatten.toMap

    val b = games.map(g => g.result.homeScore.toDouble) ++ games.map(g => g.result.awayScore.toDouble)

    val x = LSMRSolver.solve(A, games.size * 2, teamMap.size * 2 + 1, b)
    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statOff, date, s.teamByKey(pair._1), x(pair._2)).
        update(statDef, date, s.teamByKey(pair._1), x(pair._2 + teamMap.size))
    })
  }
}
