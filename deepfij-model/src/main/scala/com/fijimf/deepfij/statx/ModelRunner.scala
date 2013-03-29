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

    val r: GenericLogisticRegression = new GenericLogisticRegression(sched, new SingleStatisticFeatureMapper(pp))
    val raccuracy: Map[Date, (Double, Double)] = r.cumulativeAccuracy(sched)
    raccuracy.keys.toList.sorted.foreach(k => println(k + " --> " + raccuracy(k)))

    sched.gameList.sortBy(_.date).foreach(g => {
      println("%10s %20s %3d %20s %3d %s".format(
        g.date.toString, g.homeTeam.name, g.resultOpt.map(_.homeScore).getOrElse(0), g.awayTeam.name, g.resultOpt.map(_.awayScore).getOrElse(0), r.winProbability(g).toString
      )
      )
    })

  }
}