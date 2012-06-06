package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.Schedule
import java.util.Date


trait Statistic[K] extends StatInfo {
  statistic =>
  def keys: List[K]

  def startDate: Date

  def endDate: Date

  def function(k: K, d: Date): Option[Double]

  def population( d: Date): Population[K] = {
    new Population[K] {
      val format = statistic.format

      val statKey = statistic.statKey

      val name = statistic.name

      val higherIsBetter = statistic.higherIsBetter

      val stat = (k: K) => statistic.function(k, d)

      val date = d

      val keys = statistic.keys
    }
  }

  def series(k: K): TimeSeries[K] = {
    new TimeSeries[K] {
      val format = statistic.format

      val statKey = statistic.statKey
      val name = statistic.name

      val higherIsBetter = statistic.higherIsBetter


      val stat = (d: Date) => statistic.function(k, d)
      val key = k

      val endDate = statistic.endDate

      val startDate = statistic.startDate
    }
  }
}
