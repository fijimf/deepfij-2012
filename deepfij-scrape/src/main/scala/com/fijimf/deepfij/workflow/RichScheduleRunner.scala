package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import datasource.DataSource
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._
import org.quartz.impl.StdSchedulerFactory
import org.quartz._
import com.fijimf.deepfij.workflow.RichScheduleRunner
import impl.triggers.CronTriggerImpl
import scala.Some
import com.fijimf.deepfij.workflow.DataManager
import java.util.Date

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
 * DataSource[T]         <= source
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

object Quartz {
  val scheduler = StdSchedulerFactory.getDefaultScheduler();
  scheduler.start()

  def scheduleJob(name: String, group: String, cron: String, f: () => Unit) {

    JobBuilder.newJob().
    val job: JobDetail = new Job {
      def execute(jec: JobExecutionContext) {
        f()
      }
    }

    // Trigger the job to run now, and then repeat every 40 seconds
    val trigger: Trigger = new CronTriggerImpl(name, group, cron)
      .withIdentity("trigger1", "group1")
      .startNow()
      .withSchedule(simpleSchedule()
      .withIntervalInSeconds(40)
      .repeatForever())
      .build();

    // Tell quartz to schedule the job using our trigger
    scheduler.scheduleJob(job, trigger);

  }

  def shutdown() {
    scheduler.shutdown();
  }
}

case class DataManager[T <: KeyedObject](initializer: Option[DataSource], updater: Option[DataSource], exporter: Option[DataSource], verfifer: Option[DataSource]) {

}

case class RichScheduleRunner(key: String,
                              name: String,
                              conferenceMgr: DataManager[Conference],
                              teamMgr: List[DataSource[Team]],
                              aliasMgr: List[DataSource[Alias]],
                              gameMgr: List[DataSource[Game]],
                              resultMgr: List[DataSource[Result]]) {

  val log = Logger.getLogger(this.getClass)
  require(StringUtils.isNotBlank(name) && validName(name))
  require(StringUtils.isNotBlank(key) && validKey(key))

  log.info("Initializing ")

  val sd = new ScheduleDao
  val cd = new ConferenceDao
  val td = new TeamDao
  val ad = new AliasDao
  val gd = new GameDao
  val rd = new ResultDao

  def load[T <: KeyedObject](schedule: Schedule, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    val list: List[Map[String, String]] = ds.load
    log.info("Loaded " + list.size + " observations.")
    dao.saveAll(for (data <- list; t <- ds.build(schedule, data)) yield {
      log.info("Saving " + t.key)
      t
    })
    sd.findByKey(schedule.key).getOrElse(schedule)
  }

  def loadIfEmpty[T <: KeyedObject](schedule: Schedule, emptyTest: Schedule => Boolean, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    if (emptyTest(schedule)) {
      for (data <- ds.load; t <- ds.build(schedule, data)) {
        dao.save(t)
      }
      sd.findByKey(schedule.key).getOrElse(schedule)
    } else {
      schedule
    }
  }


  def coldStartup: RichScheduleRunner = {
    log.info("Cold startup")
    if (status != NotInitialized) {
      log.warn("Cannot call startup on an itialized ScheduleRunner")
      throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
    }
    sd.findByKey(key).map(s => {
      log.info("Dropping existing schedule with key %s".format(key))
      sd.delete(s.id)
    })
    log.info("Creating schedule with key %s".format(key))
    val schedule =
      (sd.save _).
        andThen(load[Conference](_, conferenceReaders.head, cd)).
        andThen(load[Team](_, teamReaders.head, td)).
        andThen(load[Alias](_, aliasReaders.head, ad)).
        andThen(load[Game](_, gameReaders.head, gd)).
        andThen(load[Result](_, resultReaders.head, rd)).
        apply(new Schedule(key = key, name = name))
    log.info("Initialization is complete.  Starting game/result monitor")
    copy(status = Running)
  }

  def warmStartup: RichScheduleRunner = {
    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => sd.save(new Schedule(key = key, name = name))
    }
    (sd.save _).
      andThen(loadIfEmpty[Conference](_, _.conferenceList.isEmpty, conferenceReaders.head, cd)).
      andThen(loadIfEmpty[Team](_, _.teamList.isEmpty, teamReaders.head, td)).
      andThen(loadIfEmpty[Alias](_, _.aliasList.isEmpty, aliasReaders.head, ad)).
      andThen(loadIfEmpty[Game](_, _.gameList.isEmpty, gameReaders.head, gd)).
      apply(schedule)
    log.info("Initialization is complete.  Starting game/result monitor")
    copy(status = Running)
  }


  def hotStartup: RichScheduleRunner = {
    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doesn't exist");
    }
    require(!schedule.conferenceList.isEmpty, "Hot startup failed.  No conferences are known.")
    require(!schedule.teamList.isEmpty, "Hot startup failed.  No teams are known.")
    log.info("Hot startup succeeded.  Starting game/result monitor")

    copy(status = Running)
  }

  def periodicCheck {
  }

  def periodicUpdate {
  }


}