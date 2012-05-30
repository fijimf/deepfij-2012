package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.util.DateStream

trait TimeSeries[K] extends MetaStatInfo {

  def key: K

  def startDate: Date

  def endDate: Date

  val dates=new DateStream(startDate, endDate)

  def stat: Function[Date, Option[Double]]

  val order=if (higherIsBetter)1.0 else -1.0

  lazy val rankedPairs: List[(Date,Double)] = dates.map(d=>(d->stat(d))).filter(_._2.isDefined).map(p=>(p._1, p._2.get)).toList

}
