package com.fijimf.deepfij.util

import org.apache.commons.lang.time.DateUtils
import java.util.{Calendar, Date}

case class DateStream(from: Date, to: Date) extends Stream[Date] {
  def tailDefined = !DateUtils.isSameDay(from, to)

  override def isEmpty = DateUtils.truncate(from, Calendar.DATE).after(DateUtils.truncate(to, Calendar.DATE))

  override def head = from

  override def tail = DateStream(DateUtils.addDays(from, 1), to)

}