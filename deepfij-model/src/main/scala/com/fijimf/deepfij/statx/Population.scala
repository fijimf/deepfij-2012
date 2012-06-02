package com.fijimf.deepfij.statx

import java.util.Date
import scala.math._
import org.apache.commons.math3.stat.StatUtils

trait Population[K] extends StatInfo {

  def keys: List[K]

  def date: Date

  def stat: Function[K, Option[Double]]

  val order = if (higherIsBetter) 1.0 else -1.0

  private[this] lazy val valuePairs: List[(Double, K)] = keys.map(k => (stat(k) -> k)).filter(_._1.isDefined).map(p => (p._1.get -> p._2)).sortBy(_._1 * order)
  private[this] lazy val values: List[Double] = valuePairs.map(_._1)
  private[this] lazy val valueCount: Map[Double, Int] = valuePairs.groupBy(_._1).map(tup => tup._1 -> tup._2.size)
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

  def topN(n: Int): List[(K, Double)] = {
    val x = valuePairs.take(n).lastOption
    val m = x.map(_._1).map(d => valueCount(d) + valueIndex(d)).getOrElse(0)
    valuePairs.take(m).map(p => (p._2 -> p._1))
  }

  def bottomN(n: Int): List[(K, Double)] = {
    val x = valuePairs.takeRight(n).headOption
    val m = x.map(_._1).map(d => count - valueIndex(d)).getOrElse(0)
    valuePairs.takeRight(m).map(p => (p._2 -> p._1))
  }

  def percentile(k: K): Option[Double] = rank(k).map(1 - _.toDouble / count)

  lazy val min: Option[Double] = values.lastOption

  lazy val max: Option[Double] = values.headOption

  lazy val med: Option[Double] = if (values.isEmpty) {
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

  def zScore(k: K): Option[Double] = for (t <- stat(k); m <- mean; sd <- stdDev) yield (t - m) / sd


}
