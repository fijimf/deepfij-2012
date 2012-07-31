package com.fijimf.deepfij

import data.ncaa.json.Team
import modelx.{ScheduleDao, Alias, Conference}
import org.apache.log4j.Logger
import workflow.{Deepfij, ManagerStatus}


case object NotInitialized extends ManagerStatus

case object Running extends ManagerStatus

trait ScheduleManager {
  def key: String

  def name: String

  def status: ManagerStatus

  def parent: Deepfij
}

case class NewManager(key: String,
                      name: String,

                      parent: Deepfij,
                      confReaders: List[Reader[Conference]],
                      teamReaders: List[Reader[Team]],
                      aliasReaders: List[Reader[Alias]],
                      gameReaders: List[Reader[Team]],
                      resultReaders: List[Reader[Team]]) extends ScheduleManager {
  val log = Logger.getLogger(this.getClass)
  val sd = new ScheduleDao
  val status = NotInitialized

  def verifySchedule(): ScheduleManager = {
    log.info("Schedule %s exists in database.  Verifying. ")
    new RunningManager(key, name, Running, parent, confReaders, teamReaders, aliasReaders, gameReaders, resultReaders)
  }

  def createSchedule(): ScheduleManager = {
    log.info("Schedule %s does not exist in database.  Creating. ")
    new RunningManager(key, name, Running, parent, confReaders, teamReaders, aliasReaders, gameReaders, resultReaders)
  }

  def initialize(): ScheduleManager = {
    log.info("Initializing schedle %s(%s)".format(key, name))
    sd.findByKey(key) match {
      case Some(s) => verifySchedule()
      case None => createSchedule()
    }
  }

}

case class RunningManager(key: String,
                          name: String,
                          status: ManagerStatus,
                          parent: Deepfij,
                          confReaders: List[Reader[Conference]],
                          teamReaders: List[Reader[Team]],
                          aliasReaders: List[Reader[Alias]],
                          gameReaders: List[Reader[Team]],
                          resultReaders: List[Reader[Team]]) extends ScheduleManager {
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




