package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger

/**
 * Use cases:
 * cold startup <- if exists P(drop/create) SS(verify); if not exists P(create) SS(verify)
 * warm startup <- if exists ALL(verify); if not exists P(create) SS(verify)
 * hot startup  <- ALL(skip)
 * periodic check <-
 * periodic update <-
 */

case class ScheduleManager(key: String,
                           name: String,
                           status: ManagerStatus,
                           conferenceReaders: List[DataSource[Conference]],
                           teamReaders: List[DataSource[Team]],
                           aliasReaders: List[DataSource[Alias]],
                           gameReaders: List[DataSource[Game]],
                           resultReaders: List[DataSource[Result]]) {
  require(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(key))
  require(!conferenceReaders.isEmpty && !teamReaders.isEmpty && !aliasReaders.isEmpty && !gameReaders.isEmpty && !resultReaders.isEmpty)
  val log = Logger.getLogger(this.getClass)
  val sd = new ScheduleDao
  val cd = new ConferenceDao


  def loadAliases(ds: DataSource[Alias]) {
  }

  def verifyAliases(dss: List[DataSource[Alias]]) {
  }

  def loadConferences(schedule: Schedule, ds: DataSource[Conference]) {
    for (data <- ds.load; c <- ds.build(schedule, data)) {
      cd.save(c)
    }
  }

  def verifyConferences(schedule: Schedule, ds: DataSource[Conference]) {
    val cs1: Map[String, Conference] = schedule.conferenceList.map(c => c.key -> c).toMap
    val cs2: Map[String, Conference] = (for (data <- ds.load; c <- ds.build(schedule, data)) yield {
      c.key -> c
    }).toMap
    val keys: Set[String] = cs1.keySet ++ cs2.keySet
    keys.map(k => {
      (cs1.get(k), cs2.get(k)) match {
        case (Some(c1), Some(c2)) => if (!ds.verify(c1, c2)) (Some(c1), Some(c2))
        case (_, Some(c2)) => (None, Some(c2))
        case (Some(c1), _) => (Some(c1), None)
        case (None, None) => throw new IllegalStateException()
      }
    })

  }


  def coldStartup {
    sd.findByKey(key).map(s => sd.delete(s.id))
    val schedule = sd.save(new Schedule(key = key, name = name))
    loadConferences(schedule, conferenceReaders.head)
    conferenceReaders.tail.map(cr => verifyConferences(schedule, cr))
    //    loadAliases(aliasReaders.head)
    //    verifyAliases(aliasReaders.tail)


  }

  def warmStartup {
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => sd.save(new Schedule(key = key, name = name))
    }
    if (schedule.conferenceList.isEmpty) {
      loadConferences(schedule, conferenceReaders.head)
    }
    conferenceReaders.tail.map(cr => verifyConferences(schedule, cr))
  }


  def hotStartup {
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doesn't exist");
    }
  }

  def periodicCheck {
  }

  def periodicUpdate {
  }


}