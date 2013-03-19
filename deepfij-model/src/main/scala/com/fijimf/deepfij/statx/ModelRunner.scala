package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Team, Schedule, ScheduleDao}
import com.fijimf.deepfij.statx.models._
import com.fijimf.deepfij.repo.StatisticRepository
import org.apache.log4j.Logger
import java.util.Date


object ModelRunner {
  val sd = new ScheduleDao
  val log = Logger.getLogger(this.getClass)

  def main(args: Array[String]) {
    val sched: Schedule = sd.findByKey("ncaa2013").get
    val repo: StatisticRepository = new StatisticRepository
    List(new WonLostModel, new PointsModel, new NaiveLinearRegression, new HomeAdjustedLinearRegression, new OffenseDefenseLinearRegression
    ).foreach(model => {
      log.info("Start running " + model.name)
      val statistics: Map[String, Statistic[Team]] = model.createStatistics(sched)
      log.info("Done running " + model.name)
      log.info("Start publishing " + model.name)
      repo.publish(statistics)
      log.info("Finished publishing " + model.name)
    })
  }
}

object ModelTester {
  val sd = new ScheduleDao
  val log = Logger.getLogger(this.getClass)

  def main(args: Array[String]) {
    val sched: Schedule = sd.findByKey("ncaa2013").get
    val repo: StatisticRepository = new StatisticRepository
    List(new OffenseDefenseLinearRegression).foreach(model => {
      log.info("Start running " + model.name)
      val statistics: Map[String, Statistic[Team]] = model.createStatistics(sched)
      log.info("Done running " + model.name)
      List("off-point-predictor", "def-point-predictor").foreach {
        //      List("point-predictor", "win-predictor").foreach {
        k => {
          val h: Statistic[Team] = statistics.get(k).get

          val hp: Population[Team] = h.population(h.endDate)

          sched.teamList.sortBy(t => hp.stat(t).getOrElse(0.0)).foreach(t => println("%-18s %8.4f ".format(t.key, hp.stat(t).getOrElse(Double.NaN))))

          val p = new SingleStatisticWinnerPredictor {
            def statistic = h
          }

          val accuracy: Map[Date, Double] = p.cumulativeAccuracy(sched)
          accuracy.keys.toList.sorted.foreach(k => println(k + " --> " + accuracy(k)))
        }
      }
    })

  }
}