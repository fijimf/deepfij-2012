package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, Schedule}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics


class PointsModel extends SinglePassGameModel[Team] {
  val obsTypes = List(
    ("points-for", true),
    ("points-against", false),
    ("score-margin", true),
    ("score-total", true),
    ("log-score-ratio", true)
  )

  val popStats = List("max", "min", "mean", "stdev", "sum")

  case class PointsRunning(pointsFor: List[Double], pointsAgainst: List[Double], scoreMargin: List[Double], scoreTotal: List[Double], logScoreRatio: List[Double])

  private[this] var runningTotals = Map.empty[Team, PointsRunning]

  val statistics = for ((k, hib) <- obsTypes; p <- popStats) yield StatInfoImpl(k + "-" + p, hib)

  def scheduleKeys(s: Schedule) = s.teamList.sortBy(_.name)

  def scheduleStartDate(s: Schedule) = s.gameList.minBy(_.date).date

  def scheduleEndDate(s: Schedule) = s.gameList.maxBy(_.date).date

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

        //TRICKSY -- taking advantage of teh fact that obsTypes is in the same order as PointsRunning
        obsTypes.zip(tot.productIterator.toIterable).foldLeft(ctx) {
          case (c:ModelContext[Team],((k: String, hib: Boolean), xs: List[Double])) => {
            val s = new DescriptiveStatistics(xs.toArray)
            c.update(StatInfoImpl(k + "-max", hib), d, team, s.getMax)
              .update(StatInfoImpl(k + "-min", hib), d, team, s.getMin)
              .update(StatInfoImpl(k + "-mean", hib), d, team, s.getMean)
              .update(StatInfoImpl(k + "-stdev", hib), d, team, s.getStandardDeviation)
              .update(StatInfoImpl(k + "-sum", hib), d, team, s.getSum)
          }
        }
      }
    }
  }
}