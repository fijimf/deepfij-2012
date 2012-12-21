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

  def update(ctx: ModelContext[T]): ModelContext[T] = {
    val keys: Iterable[StatInfo] = ctx.stats.keys

    val updatedStats: Map[StatInfo, ModelValues[T]] = keys.foldLeft(stats)((ss: Map[StatInfo, ModelValues[T]], si: StatInfo) => {
      ss.get(si) match {
        case Some(mv: ModelValues[T]) => ss + (si -> mv.update(ctx.stats(si)))
        case None => ss + (si -> ctx.stats(si))
      }
    })
    ModelContext(updatedStats)
  }

}