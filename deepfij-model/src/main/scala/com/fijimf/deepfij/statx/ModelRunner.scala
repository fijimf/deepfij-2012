package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.statx.models._
import com.fijimf.deepfij.repo.StatisticRepository
import org.apache.log4j.Logger
import java.util.Date
import predictor._


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
    val wp: Statistic[Team] = repo.tsd.statistic("win-predictor")
    val pp: Statistic[Team] = repo.tsd.statistic("point-predictor")
    val hp: Statistic[Team] = repo.tsd.statistic("homadj-point-predictor")
    val sp: Statistic[Team] = repo.tsd.statistic("score-margin-mean")
    val ss: Statistic[Team] = repo.tsd.statistic("streak")

    val r: GenericLogisticRegression = new GenericLogisticRegression(sched, new SingleStatisticPolyFeatureMapper(ss))
    val raccuracy: Map[Date, Accuracy] = r.cumulativeAccuracy(sched)
    raccuracy.keys.toList.sorted.foreach(k => println(k + " --> " + raccuracy(k)))

  }
}