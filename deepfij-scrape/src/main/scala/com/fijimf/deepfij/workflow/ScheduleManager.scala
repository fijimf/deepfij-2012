package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import scala.Some
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
                           parent: Deepfij,
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

  def loadConferences(ds: DataSource[Conference]) {
    for (data <- ds.load; c <- ds.build(data)) {
      cd.save(c)
    }
  }

  def verifyConferences(dss: List[DataSource[Conference]]) {
    for (ds <- dss) {
    }
  }
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
        case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doesn't exist");
      }
    }

    def periodicCheck {}

    def periodicUpdate {}


  }