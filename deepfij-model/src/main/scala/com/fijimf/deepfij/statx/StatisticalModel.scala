package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.Schedule
import java.util.Date

trait StatisticalModel[T] {

  def statistics: List[StatInfo]

  def initialize(): ModelContext[T] = ModelContext[T](statistics.map(k => (k -> ModelValues[T]())).toMap)

  def process(s: Schedule, ctx: ModelContext[T]): ModelContext[T] = ctx

  def complete(ctx: ModelContext[T]): ModelContext[T] = ctx

  def scheduleKeys(s:Schedule):List[T]

  def scheduleStartDate(s:Schedule):Date

  def scheduleEndDate(s:Schedule):Date

  def createStatistics(s: Schedule): Map[String, Statistic[T]] = {
    val ctx: ModelContext[T] = complete(process(s, initialize()))

    statistics.map(k=> (k.name -> new Statistic[T] {
      def name = k.name

      def higherIsBetter = k.higherIsBetter

      def keys = scheduleKeys(s)

      def startDate = scheduleStartDate(s)

      def endDate = scheduleEndDate(s)

      def function(t: T, d: Date) = {
        ctx.get(k,d,t)
      }

    })).toMap
  }
}

