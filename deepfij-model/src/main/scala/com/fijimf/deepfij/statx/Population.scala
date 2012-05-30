package com.fijimf.deepfij.statx

import java.util.Date
import scala.math._
import org.apache.commons.math3.stat.StatUtils

trait Population[K] extends MetaStatInfo {

  def keys: List[K]

  def date: Date

  def stat: Function[K, Option[Double]]

  val order = if (higherIsBetter) 1.0 else -1.0

  private[this] lazy val values: List[Double] = keys.map(k => stat(k)).filter(_.isDefined).map(_.get).sortBy((_ * order))
  private[this] lazy val valueCount: Map[Double, Int] = values.groupBy(x => x).map(tup => tup._1 -> tup._2.size)
  private[this] lazy val valueIndex: Map[Double, Int] = valueCount.keys.map(x => (x -> values.indexOf(x))).toMap

  def rank(k: K): Option[Double] = {
    for (x <- stat(k);
         i <- valueIndex.get(x)) yield (i + 1.0)
  }

  def fractionalRank(k: K): Option[Double] = {
    for (x <- stat(k);
         i <- valueIndex.get(x);
         n <- valueCount.get(x)) yield (1.0 + i) + ((n - 1.0) / 2.0)
  }


  //
  //  def zScore(k: K): Option[Double] = for (t <- stat.lift(t); m <- mean; sd <- stdDev) yield (t - m) / sd
  //
  //  def percentile(t: K): Option[Double] = rank(t).map(_.toDouble / ranked.size)
  //
  //  def count = ranked.size
  //
  def min: Option[Double] = values.lastOption
  def max: Option[Double] = values.headOption
//  def minItem: List[String] = values.headOption.map()
//  def maxItem: List[String] = values.lastOption

    def med: Option[Double] = if (values.isEmpty) {
      None
    } else {
      val n = values.size
      Some(n % 2 match {
        case 0 => (values(n / 2) + values(n / 2 - 1)) / 2.0
        case 1 => values(n / 2)
      })
    }

    lazy val count: Int = values.size
    lazy val missing: Int = keys.size - count

    lazy val mean: Option[Double] = if (values.isEmpty) {
      None
    } else {
      Some(StatUtils.mean(values.toArray))
    }

    lazy val stdDev: Option[Double] = if (values.isEmpty) {
      None
    } else {
      Some(sqrt(StatUtils.populationVariance(values.toArray)))
    }

}
