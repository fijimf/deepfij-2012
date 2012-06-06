package com.fijimf.deepfij.statx

import java.util.Date

case class StatisticMap[T](statKey:String, name: String, format:String, higherIsBetter: Boolean, data: Map[(Date, T), Double]) extends Statistic[T] {
  val (d, k) = data.keys.unzip

  def keys = k.toSet.toList

  def startDate = d.min

  def endDate = d.max

  def function(k: T, d: Date) = data.get((d, k))
}
