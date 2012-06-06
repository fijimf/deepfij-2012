package com.fijimf.deepfij.repo

import com.fijimf.deepfij.statx.Statistic
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.modelx._


class StatisticRepository extends Transactional {
  val msd = new MetaStatDao
  val tsd = new TeamStatDao

  def publish(statistics: Map[String, Statistic[Team]]) {
    statistics.foreach {
      case (k: String, statistic: Statistic[Team]) => {
        publish(statistic)
      }
    }
  }

  def publish(statistic: Statistic[Team]) {
    transactional {
      msd.findByStatKey(statistic.statKey).foreach(m => msd.delete(m.id))
      val ms = msd.save(new MetaStat(name = statistic.name, statKey = statistic.statKey, format = statistic.format, higherIsBetter = statistic.higherIsBetter))
      for (d <- DateStream(statistic.startDate, statistic.endDate);
           t <- statistic.keys;
           x <- statistic.function(t, d)) {
        tsd.save(new TeamStat(metaStat = ms, team = t, date = d, value = x))
      }
    }
  }
}
