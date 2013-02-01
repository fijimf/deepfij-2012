package com.fijimf.deepfij.statx

import java.util.Date

/**
 * Container which holds all of the values generated by a StatisticalModel for a given schedule.  StatInfo holds metadata
 * about the item generated, and the ModelValues hold a map of Dates to Populations
 *
 * @param stats Map of StatInfo metadata about the statistic to ModelValues
 * @tparam T  The type for which statistics are being generated -- typically Team, but could conceivably be Game,
 *            Schedule or Date
 */
case class ModelContext[T](stats: Map[StatInfo, ModelValues[T]] = Map.empty[StatInfo, ModelValues[T]], parameters: Map[StatInfo, ModelValues[String]] = Map.empty[StatInfo, ModelValues[String]]) {
  require(stats.keySet == parameters.keySet)

  def get(k: StatInfo, d: Date, s: T): Option[Double] = for (c <- stats.get(k); x <- c.get(d, s)) yield x

  def getParam(k: StatInfo, d: Date, s: String): Option[Double] = for (c <- parameters.get(k); x <- c.get(d, s)) yield x

  def update(k: StatInfo, d: Date, s: T, x: Double): ModelContext[T] = {
    val revisedValues: ModelValues[T] = stats.get(k) match {
      case Some(m) => m.update(d, s, x)
      case None => ModelValues().update(d, s, x)
    }
    val revisedStats = stats + (k -> revisedValues)
    copy(stats = revisedStats)
  }

  def updateParm(k: StatInfo, d: Date, s: String, x: Double): ModelContext[T] = {
    val revisedValues: ModelValues[String] = parameters.get(k) match {
      case Some(m) => m.update(d, s, x)
      case None => ModelValues().update(d, s, x)
    }
    val revisedParams = parameters + (k -> revisedValues)
    copy(parameters = revisedParams)
  }

  def update(ctx: ModelContext[T]): ModelContext[T] = {
    val keys: Iterable[StatInfo] = ctx.stats.keys

    val updatedStats: Map[StatInfo, ModelValues[T]] = keys.foldLeft(stats)((ss: Map[StatInfo, ModelValues[T]], si: StatInfo) => {
      ss.get(si) match {
        case Some(mv: ModelValues[T]) => ss + (si -> mv.update(ctx.stats(si)))
        case None => ss + (si -> ctx.stats(si))
      }
    })

    val updatedParms: Map[StatInfo, ModelValues[String]] = keys.foldLeft(parameters)((ss: Map[StatInfo, ModelValues[String]], si: StatInfo) => {
      ss.get(si) match {
        case Some(mv: ModelValues[String]) => ss + (si -> mv.update(ctx.parameters(si)))
        case None => ss + (si -> ctx.parameters(si))
      }
    })
    ModelContext(updatedStats, updatedParms)
  }

}