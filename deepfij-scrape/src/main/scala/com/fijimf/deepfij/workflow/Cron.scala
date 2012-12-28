package com.fijimf.deepfij.workflow

import it.sauronsoftware.cron4j.Scheduler
import org.apache.log4j.Logger

/**
 * Solves the following problem.
 *
 * In the general case:
 *
 * We want to instantiate some aggregate type, composed of other related component types on startup
 * and (potentially) keep the aggregate type current.  The resources we have are
 * (1) Persistence we own, viz. a database;
 * (2) One or more primary sources which we don't own;
 * (3) Knowledge that the components of our Schedule aggregate can be identified by a unique, domain specific key;
 * (4) Ability to construct component objects given the primary source and (possibly partially constructed) aggregate;
 *
 * To be explicit:
 * Schedule              <= aggregate
 * T <: KeyedObject, ... <= types
 * BaseDao[T,_]          <= persistence
 *
 * Given those parameters we have three main use cases
 *
 * (1) Cold startup <- Rebuild everything; drop any existing data in the database
 * A. Drop/Create aggregate type from the database
 * B. In order, over the list of component types
 * 1. Persist them to the database
 * (2) Warm startup <- Static data is good; reload dynamic data
 * (3) Hot startup  <- Quick restart all data in the database is good and we can just start updater tasks
 *
 * we now have a number of different types of "DataSources"
 * 1) initializers
 * 2) updaters
 * 3) exporters
 * 4) verifiers
 *
 */

object Cron {
  val log = Logger.getLogger(this.getClass)

  private var tasks = List.empty[String]
  val scheduler = new Scheduler
  scheduler.start()

  scheduler.schedule("*/2 * * * *", new Runnable() {
    def run() {
      log.info("Cron Heartbeat.  Known tasks are " + tasks.mkString(", "))
    }
  })

  def scheduleJob(cron: String, f: () => Unit): String = {
    val id = scheduler.schedule(cron, new Runnable() {
      def run() {
        f()
      }
    })
    tasks = id :: tasks
    id
  }

  def shutdown() {
    scheduler.stop()
  }
}
