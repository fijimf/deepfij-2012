package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.Schedule
import java.util.Date

trait Statistic[K] extends MetaStatInfo {
  parent =>
  def keys(s: Schedule): List[K]

  def startDate(s: Schedule): Date

  def endDate(s: Schedule): Date

  def population(s: Schedule, d: Date): Population[K] = {
    new Population[K] {

      val name = parent.name

      val higherIsBetter = parent.higherIsBetter

      val stat = (k:K)=>parent.function(s, k, d)

      val date = d

      val keys = parent.keys(s)
    }
  }

  def series(s: Schedule, k: K): TimeSeries[K] = {
    new TimeSeries[K] {

      val name = parent.name

      val higherIsBetter = parent.higherIsBetter


      val stat = (d:Date)=>parent.function(s, k, d)
      val key = k

      val endDate = parent.endDate(s)

      val startDate = parent.startDate(s)
    }
  }

  def function(s: Schedule, k: K, d: Date): Option[Double]
}
