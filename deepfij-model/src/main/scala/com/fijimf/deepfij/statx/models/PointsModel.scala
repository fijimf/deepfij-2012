package com.fijimf.deepfij.statx.models

import java.util.Date
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import com.fijimf.deepfij.statx.{ModelValues, TeamModel, ModelContext, SinglePassGameModel}
import com.fijimf.deepfij.modelx.{MetaStat, Game, Team}


class PointsModel extends SinglePassGameModel[Team] with TeamModel {

  case class ObservationType(description: String, key: String, higherIsBetter: Boolean, f: (PointsRunning) => List[Double])

  val observationTypes = List(
    ObservationType("Points For", "points-for", true, _.pointsFor),
    ObservationType("Points Against", "points-against", false, _.pointsAgainst),
    ObservationType("Margin", "score-margin", true, _.margin),
    ObservationType("Total Score", "score-total", true, _.totalScore)
  )

  case class PopulationMeasure(description: String, key: String, format: String, f: (DescriptiveStatistics) => Double)

  val populationMeasures = List(
    PopulationMeasure("Max", "max", "%4.0f", _.getMax),
    PopulationMeasure("Min", "min", "%4.0f", _.getMin),
    PopulationMeasure("Mean", "mean", "%8.3f", _.getMean),
    PopulationMeasure("Std Dev", "stdev", "%9.3f", _.getStandardDeviation),
    PopulationMeasure("Sum", "sum", "%6.0f", _.getSum))

  case class PointsRunning(pfpa: List[(Double, Double)] = List.empty[(Double, Double)]) {
    def update(pf: Double, pa: Double): PointsRunning = copy(pfpa = (pf, pa) :: pfpa)

    lazy val pointsFor = pfpa.map(_._1)
    lazy val pointsAgainst = pfpa.map(_._2)
    lazy val margin = pfpa.map(tup => tup._1 - tup._2)
    lazy val totalScore = pfpa.map(tup => tup._1 + tup._2)
  }

  private[this] var runningTotals = Map.empty[Team, PointsRunning].withDefaultValue(PointsRunning())

  val modelStatistic = (
    for (o <- observationTypes; p <- populationMeasures) yield new MetaStat(
      name = o.description + " " + p.description,
      statKey = o.key + "-" + p.key,
      format = p.format,
      higherIsBetter = o.higherIsBetter)
    )

  val statistics = modelStatistic

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    for (g <- gs; r <- g.resultOpt) {
      runningTotals += (g.homeTeam -> runningTotals(g.homeTeam).update(r.homeScore, r.awayScore))
      runningTotals += (g.awayTeam -> runningTotals(g.awayTeam).update(r.homeScore, r.awayScore))
    }
    val data: List[(MetaStat, ModelValues[Team])] = for (o <- observationTypes; p <- populationMeasures) yield {
      val m = new MetaStat(
        name = o.description + " " + p.description,
        statKey = o.key + "-" + p.key,
        format = p.format,
        higherIsBetter = o.higherIsBetter)
      m -> ModelValues[Team](values = Map(d -> runningTotals.keys.map(t => t -> p.f(new DescriptiveStatistics(o.f(runningTotals(t)).))).toMap))
    }
    data.foldLeft(ctx)((context: ModelContext[Team], tup: (MetaStat, ModelValues[Team])) => context.copy(stats = context.stats + (tup._1 -> tup._2)))
  }
}