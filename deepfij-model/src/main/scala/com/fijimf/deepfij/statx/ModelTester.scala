package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Team, Schedule, ScheduleDao}
import java.util.Date
import java.text.SimpleDateFormat
import models.PointsModel


object ModelTester {
  val sd = new ScheduleDao

  def main(args: Array[String]) {
    println("Hello")
    val sched: Schedule = sd.findByKey("ncaa2012").get
    println(sched.teamList.size)
    println(sched.gameList.size)
    val model: PointsModel = new PointsModel
    println("Starting" + new Date)
    val statistics: Map[String, Statistic[Team]] = model.createStatistics(sched)
    println("Ending " + new Date)
    println(statistics.keys.mkString(","))
    val atop20: List[(Team, Double)] = statistics("log-score-ratio-mean").population(new SimpleDateFormat("yyyyMMdd").parse("20120121")).topN(20)
    val btop20: List[(Team, Double)] = statistics("log-score-ratio-mean").population(new SimpleDateFormat("yyyyMMdd").parse("20120221")).topN(20)
    val ctop20: List[(Team, Double)] = statistics("log-score-ratio-mean").population(new SimpleDateFormat("yyyyMMdd").parse("20120321")).topN(20)
    println(atop20.map{case (team: Team, d: Double) => "%-32s %5.2f".format(team.name, d)}.mkString("\n"))
    println(btop20.map{case (team: Team, d: Double) => "%-32s %5.2f".format(team.name, d)}.mkString("\n"))
    println(ctop20.map{case (team: Team, d: Double) => "%-32s %5.2f".format(team.name, d)}.mkString("\n"))
  }
}