package com.fijimf.deepfij.server

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._

class Boot {
  System.setProperty("deepfij.persistenceUnitName", "deepfij")
  val mainModule = new DeepFijService {

    val activeScheduleKey="2012"

    def start() {

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