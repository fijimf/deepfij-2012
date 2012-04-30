package com.fijimf.deepfij.server

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import com.fijimf.deepfij.modelx.PersistenceSource
import org.apache.log4j.Logger

class Boot {
  val log = Logger.getLogger(this.getClass)
  System.setProperty("deepfij.persistenceUnitName", "deepfij")
  val mainModule = new DeepFijService {

    val activeScheduleKey="2012"

    def start() {
      if (!PersistenceSource.testDatabase()){
        log.info("Test database failed.  Building new DB")
        PersistenceSource.buildDatabase()
        log.info("Done building DB")
      }
    }

    def shutdown() {

    }
  }

  val service = actorOf(new HttpService(mainModule.service))
  val rootService = actorOf(new RootService(service))

  // start and supervise the created actors
  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(service, Permanent),
        Supervise(rootService, Permanent)
      )
    )
  ).start
}