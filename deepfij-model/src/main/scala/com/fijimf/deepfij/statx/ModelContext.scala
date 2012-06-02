package com.fijimf.deepfij.statx

import java.util.Date

case class ModelContext[T](stats: Map[StatInfo, ModelValues[T]] = Map.empty[StatInfo, ModelValues[T]]) {

  def get(k: StatInfo, d: Date, s: T): Option[Double] = for (c <- stats.get(k); x <- c.get(d, s)) yield x

  def update(k: StatInfo, d: Date, s: T, x: Double): ModelContext[T] = {
    val revisedValues: ModelValues[T] = stats.get(k) match {
      case Some(m) => m.update(d, s, x)
      case None => ModelValues().update(d, s, x)
    }
    val revisedStats = stats + (k -> revisedValues)
    ModelContext(revisedStats)
  }
}