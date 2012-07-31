package com.fijimf.deepfij

import data.ncaa.json.Team
import modelx.{Schedule, ScheduleDao, Alias, Conference}
import org.apache.log4j.Logger
import workflow.{Deepfij, ManagerStatus}
import org.apache.commons.lang.StringUtils


case object NotInitialized extends ManagerStatus

case object Running extends ManagerStatus


case class ScheduleManager(key: String,
                           name: String,
                           parent: Deepfij,
                           status: ManagerStatus,
                           conferenceReaders: List[Reader[Conference]],
                           teamReaders: List[Reader[Team]],
                           aliasReaders: List[Reader[Alias]],
                           gameReaders: List[Reader[Team]],
                           resultReaders: List[Reader[Team]]) {
  require(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(key))
  require(!conferenceReaders.isEmpty && !teamReaders.isEmpty && !aliasReaders.isEmpty && !gameReaders.isEmpty && !resultReaders.isEmpty)
  val log = Logger.getLogger(this.getClass)
  val sd = new ScheduleDao


  def loadConferences(reader: Reader[Conference])

  def coldStartup {
    sd.findByKey(key).map(s => sd.delete(s.id))
    val schedule = sd.save(new Schedule(key = key, name = name))
    loadConferences(conferenceReaders.head)
    verifyConferences(conferenceReaders.tail)
    loadAliases(aliasReaders.head)
    verifyAliases(aliasReaders.tail)


  }

  def warmStartup {
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => sd.save(new Schedule(key = key, name = name))
    }
    if (schedule.conferenceList.isEmpty) {
      loadConferences(conferenceReaders.head)
    }
    verifyConferences(conferenceReaders.tail)
  }


  def hotStartup {
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doewsn't exist");
    }
  }

  def periodicCheck {}

  def periodicUpdate {}


}


trait Reader[T] {
  def init: Int = 0


}

/**
 * Use cases:
 * cold startup <- if exists P(drop/create) SS(verify); if not exists P(create) SS(verify)
 * warm startup <- if exists ALL(verify); if not exists P(create) SS(verify)
 * hot startup  <- ALL(skip)
 * periodic check <-
 * periodic update <-
 */




