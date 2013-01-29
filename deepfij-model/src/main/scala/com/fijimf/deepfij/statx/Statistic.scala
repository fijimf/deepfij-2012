package com.fijimf.deepfij.statx

import java.util.Date


trait Statistic[K] extends StatInfo {
  outer: StatInfo =>
  def keys: List[K]

  def startDate: Date

  def endDate: Date

  def function(k: K, d: Date): Option[Double]

  def population(d: Date): Population[K] = {
    new Population[K] {
      val format = outer.format

      val modelKey = outer.modelKey

      val modelName = outer.modelName

      val statKey = outer.statKey

      val name = outer.name

      val higherIsBetter = outer.higherIsBetter

      val stat = (k: K) => outer.function(k, d)

      val date = d

      val keys = outer.keys
    }
  }

  def series(k: K): TimeSeries[K] = {
    new TimeSeries[K] {
      val format = outer.format

      val modelKey = outer.modelKey

      val modelName = outer.modelName

      val statKey = outer.statKey

      val name = outer.name

      val higherIsBetter = outer.higherIsBetter

      val stat = (d: Date) => outer.function(k, d)

      val key = k

      val endDate = outer.endDate

      val startDate = outer.startDate
    }
  }
}
