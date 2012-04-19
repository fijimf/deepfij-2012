package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.Team
import scala.math._
import org.apache.commons.math3.stat.descriptive.{DescriptiveStatistics, SummaryStatistics}
import org.apache.commons.math3.stat.StatUtils


trait Pop {
  def teams: List[Team]

  def stat: PartialFunction[Team, Double]

  lazy val ranked: List[Team] = teams.filter(stat.isDefinedAt(_)).sortBy(stat(_))


  def rank(t: Team): Option[Int] = ranked.indexOf(t) match {
    case -1 => None
    case i => Some(i)
  }

  def zScore(t: Team): Option[Double] = for (t <- stat.lift(t); m <- mean; sd <- stdDev) yield (t - m) / sd


  def percentile(t: Team): Option[Double] = rank(t).map(_.toDouble / ranked.size)

  def count = ranked.size


  def min: Option[Double] = ranked.headOption.map(stat(_))

  def max: Option[Double] = ranked.lastOption.map(stat(_))

  def med: Option[Double] = if (ranked.isEmpty) {
    None
  } else {
    Some(ranked.size % 2 match {
      case 0 => (stat(ranked(count / 2)) + stat(ranked(count / 2 - 1))) / 2.0
      case 1 => stat(ranked(count / 2))
    })
  }

  def mean: Option[Double] = if (ranked.isEmpty) {
    None
  } else {
    Some(StatUtils.mean(ranked.map(stat.lift(_)).flatten.toArray))
  }

  def stdDev: Option[Double] = if (ranked.isEmpty) {
    None
  } else {
    Some(sqrt(StatUtils.populationVariance(ranked.map(stat.lift(_)).flatten.toArray)))
  }

}
