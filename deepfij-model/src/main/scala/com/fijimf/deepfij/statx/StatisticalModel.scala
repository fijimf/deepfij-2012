package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.Schedule
import java.util.Date

trait StatisticalModel[T] {
  def name: String

  def key: String

  def statistics: List[StatInfo]

  def initialize(): ModelContext[T] = ModelContext[T](statistics.map(k => (k -> ModelValues[T]())).toMap)

  def process(s: Schedule, ctx: ModelContext[T], from: Option[Date] = None, to: Option[Date] = None): ModelContext[T] = ctx

  def complete(ctx: ModelContext[T]): ModelContext[T] = ctx

  def parameterKeys: List[String]

  def scheduleKeys(s: Schedule): List[T]

  def scheduleStartDate(s: Schedule): Date

  def scheduleEndDate(s: Schedule): Date

  def createStatistics(s: Schedule): Map[String, Statistic[T]] = {
    val ctx: ModelContext[T] = complete(process(s, initialize()))

    statistics.map(k => (k.statKey -> new Statistic[T] {
      val format = k.format

      val statKey = k.statKey

      val name = k.name

      val modelKey = k.modelKey

      val modelName = k.modelName

      val higherIsBetter = k.higherIsBetter

      val keys = scheduleKeys(s)

      val parameterKeys = ctx.parameters(k).values.values.map(_.keySet).flatten.toList

      val startDate = scheduleStartDate(s)

      val endDate = scheduleEndDate(s)

      def function(t: T, d: Date) = {
        ctx.get(k, d, t)
      }

      def parameter(s: String, d: Date) = {
        ctx.getParam(k, d, s)
      }
    })).toMap
  }
}

