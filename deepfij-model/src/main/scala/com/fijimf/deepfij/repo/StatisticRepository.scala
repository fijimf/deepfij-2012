package com.fijimf.deepfij.repo

import com.fijimf.deepfij.statx.Statistic
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.modelx._
import org.apache.log4j.Logger


class StatisticRepository extends Transactional {
  val logger = Logger.getLogger(this.getClass)

  val entityManager = PersistenceSource.entityManager

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
    logger.info("Publishing " + statistic.statKey)
    transactional {
      msd.findByStatKey(statistic.statKey).foreach(m => msd.delete(m.id))
    }
    val ms = msd.save(new MetaStat(name = statistic.name, statKey = statistic.statKey, format = statistic.format, higherIsBetter = statistic.higherIsBetter))
    for (d <- DateStream(statistic.startDate, statistic.endDate)) {
      transactional {
        for (t <- statistic.keys;
             x <- statistic.function(t, d)) {
          if (x.isInfinite || x.isNaN) {
            logger.warn("Skipping %s %s %s ==> %f".format(statistic.statKey, t.key, d.toString, x))
          } else {
            tsd.save(new TeamStat(metaStat = ms, team = t, date = d, value = x))
          }
        }
      }
    }
  }
}
