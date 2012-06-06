package com.fijimf.deepfij.statx.models

import java.util.Date
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import com.fijimf.deepfij.statx.{TeamModel, ModelContext, SinglePassGameModel}
import com.fijimf.deepfij.modelx.{MetaStat, Game, Team}


class PointsModel extends SinglePassGameModel[Team] with TeamModel {
  val obsTypes = List(
    ("Points For", "points-for", true),
    ("Points Against", "points-against", false),
    ("Margin", "score-margin", true),
    ("Total Score", "score-total", true),
    ("Log Scoring Ratio", "log-score-ratio", true)
  )

  val popStats = List(("Max", "max", "%4.0f"), ("Min", "min", "%4.0f"), ("Mean", "mean", "%8.3f"), ("Std Dev", "stdev", "%9.3f"), ("Sum", "sum", "%6.0f"))

  case class PointsRunning(pointsFor: List[Double], pointsAgainst: List[Double], scoreMargin: List[Double], scoreTotal: List[Double], logScoreRatio: List[Double])

  private[this] var runningTotals = Map.empty[Team, PointsRunning]

  val statisticLookup = (
    for ((n, k, hib) <- obsTypes; (pn, p, fmt) <- popStats)
    yield (k, p) -> new MetaStat(name = pn + " " + n, statKey = k + "-" + p, format = fmt, higherIsBetter = hib)
    ).toMap

  val statistics = statisticLookup.values.toList

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    gs.filter(g => (g.resultOpt.isDefined)).map(g => {
      val r = g.resultOpt.get
      val home = runningTotals.getOrElse(g.homeTeam, PointsRunning(List.empty[Double], List.empty[Double], List.empty[Double], List.empty[Double], List.empty[Double]))
      runningTotals += (g.homeTeam -> PointsRunning(
        r.homeScore :: home.pointsFor,
        r.awayScore :: home.pointsAgainst,
        (r.homeScore - r.awayScore) :: home.scoreMargin,
        (r.homeScore + r.awayScore) :: home.scoreTotal,
        scala.math.log(r.homeScore / r.awayScore) :: home.logScoreRatio
      ))
      val away = runningTotals.getOrElse(g.awayTeam, PointsRunning(List.empty[Double], List.empty[Double], List.empty[Double], List.empty[Double], List.empty[Double]))
      runningTotals += (g.awayTeam -> PointsRunning(
        r.awayScore :: away.pointsFor,
        r.homeScore :: away.pointsAgainst,
        (r.awayScore - r.homeScore) :: away.scoreMargin,
        (r.awayScore + r.homeScore) :: away.scoreTotal,
        scala.math.log(r.awayScore / r.homeScore) :: away.logScoreRatio
      ))
    })
    runningTotals.keys.foldLeft(ctx) {
      (ctx, team) => {
        val tot = runningTotals(team)

        //TRICKSY -- taking advantage of the fact that obsTypes is in the same order as PointsRunning
        obsTypes.zip(tot.productIterator.toIterable).foldLeft(ctx) {
          case (c: ModelContext[Team], ((n:String, k: String, hib: Boolean), xs: List[Double])) => {
            val s = new DescriptiveStatistics(xs.toArray)
            c.update(statisticLookup(k, "max"), d, team, s.getMax)
              .update(statisticLookup(k, "min"), d, team, s.getMin)
              .update(statisticLookup(k, "mean"), d, team, s.getMean)
              .update(statisticLookup(k, "stdev"), d, team, s.getStandardDeviation)
              .update(statisticLookup(k, "sum"), d, team, s.getSum)
          }
        }
      }
    }
  }
}