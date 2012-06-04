package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Team, Schedule, ScheduleDao}
import java.util.Date
import java.text.SimpleDateFormat


object ModelTester {
  val sd = new ScheduleDao

  def main(args: Array[String]) {
    println("Hello")
    val sched: Schedule = sd.findByKey("ncaa2012").get
    println(sched.teamList.size)
    println(sched.gameList.size)
    val model: WonLostModel = new WonLostModel
    println("Starting" + new Date)
    val statistics: Map[String, Statistic[Team]] = model.createStatistics(sched)
    println("Ending " + new Date)
    println(statistics.keys.mkString(","))
    val top20: List[(Team, Double)] = statistics("wp").population(new SimpleDateFormat("yyyyMMdd").parse("20120328")).topN(20)
    println(top20.mkString("\n"))
  }
}