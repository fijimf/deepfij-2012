package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{TeamStatDao, Team, Schedule, ScheduleDao}
import java.util.Date
import java.text.SimpleDateFormat
import com.fijimf.deepfij.statx.models.{LogisticRegression, LinearRegression, WonLostModel, PointsModel}
import com.fijimf.deepfij.repo.StatisticRepository
import org.apache.log4j.Logger


object ModelRunner {
  val sd = new ScheduleDao
  val log = Logger.getLogger(this.getClass)

  def main(args: Array[String]) {
    val sched: Schedule = sd.findByKey("ncaa2013").get
    val repo: StatisticRepository = new StatisticRepository
//    List(new WonLostModel, new PointsModel, new LinearRegression).foreach(model => {
    List(new LogisticRegression).foreach(model => {
      log.info("Start running " + model.name)
      val statistics: Map[String, Statistic[Team]] = model.createStatistics(sched)
      log.info("Done running " + model.name)
      log.info("Start publishing " + model.name)
      repo.publish(statistics)
      log.info("Finished publishing " + model.name)
    })
  }
}