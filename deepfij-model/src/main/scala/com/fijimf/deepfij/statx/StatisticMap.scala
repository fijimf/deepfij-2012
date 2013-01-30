package com.fijimf.deepfij.statx

import java.util.Date
import org.apache.log4j.Logger

case class StatisticMap[T](modelKey: String, modelName: String, statKey: String, name: String, format: String, override val higherIsBetter: Boolean, data: Map[(Date, T), Double], parameters: Map[(Date, String), Double]) extends Statistic[T] {
  val log = Logger.getLogger(this.getClass)
  val (d, k) = data.keys.unzip

  def keys = k.toSet.toList

  def startDate = d.min

  def endDate = d.max

  def function(k: T, d: Date) = data.get((d, k))

  def parameter(s: String, d: Date) = parameters.get((d, s))
}
