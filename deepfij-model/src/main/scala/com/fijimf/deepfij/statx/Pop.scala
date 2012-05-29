package com.fijimf.deepfij.statx

import scala.math._
import org.apache.commons.math3.stat.StatUtils
import com.fijimf.deepfij.modelx.{Schedule, Team}
import java.util.Date

trait MetaStatInfo {
  def name: String

  def higherIsBetter: Boolean
}

trait Statistic[K] extends MetaStatInfo {

  def keys(s: Schedule): List[K]

  def startDate(s: Schedule): Date

  def endDate(s: Schedule): Date

  def population(s: Schedule, d: Date): Population[K] = {
    new Population[K] {
      def function = function(s, d, _)

      def date = d

      def keys = keys
    }
  }

  def series(s: Schedule, k: K): Series[K] = {
    new Series[K] {
      def function = function(s, _, k)

      def key = k

      def endDate = endDate

      def startDate = startDate
    }
  }

  def function(s: Schedule, k: K, d: Date): Option[Double]
}

trait TeamStatistic extends Statistic[Team] {
  override def keys(s: Schedule) = s.teamList
}


trait Series[K] {
  def key: K

  def startDate: Date

  def endDate: Date

  def function: Function[Date, Option[Double]]
}

trait Population[K] {
  def keys: List[K]

  def date: Date

  def function: Function[K, Option[Double]]

  lazy val ranked: List[(K,Double)] = keys.map(k=>(k->function(k))).filter(_._2.isDefined).map(p=>(p._1, p._2.get).sortBy((_._2)))

  def rank(t: K): Option[Int] = ranked.map(_._1).indexOf(t) match {
    case -1 => None
    case i => Some(i)
  }

  def zScore(t: K): Option[Double] = for (t <- stat.lift(t); m <- mean; sd <- stdDev) yield (t - m) / sd

  def percentile(t: K): Option[Double] = rank(t).map(_.toDouble / ranked.size)

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
