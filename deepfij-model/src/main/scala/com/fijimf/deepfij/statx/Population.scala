package com.fijimf.deepfij.statx

import java.util.Date

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

  //  lazy val rankings: Map[K,]
  //
  //  def rank(t: K): Option[Int] = rankedPairs.map(_._1).indexOf(t) match {
  //    case -1 => None
  //    case i => Some(i)
  //  }
  //
  //  def zScore(k: K): Option[Double] = for (t <- stat.lift(t); m <- mean; sd <- stdDev) yield (t - m) / sd
  //
  //  def percentile(t: K): Option[Double] = rank(t).map(_.toDouble / ranked.size)
  //
  //  def count = ranked.size
  //
  //  def min: Option[Double] = ranked.headOption.map(stat(_))
  //
  //  def max: Option[Double] = ranked.lastOption.map(stat(_))
  //
  //  def med: Option[Double] = if (ranked.isEmpty) {
  //    None
  //  } else {
  //    Some(ranked.size % 2 match {
  //      case 0 => (stat(ranked(count / 2)) + stat(ranked(count / 2 - 1))) / 2.0
  //      case 1 => stat(ranked(count / 2))
  //    })
  //  }
  //
  //  def mean: Option[Double] = if (ranked.isEmpty) {
  //    None
  //  } else {
  //    Some(StatUtils.mean(ranked.map(stat.lift(_)).flatten.toArray))
  //  }
  //
  //  def stdDev: Option[Double] = if (ranked.isEmpty) {
  //    None
  //  } else {
  //    Some(sqrt(StatUtils.populationVariance(ranked.map(stat.lift(_)).flatten.toArray)))
  //  }

}