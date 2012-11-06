package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import datasource.DataSource
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._

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
 * TODO implement verify steps -- that is if we have multiple sources we need a method of creating insert/update/delete
 * from the primary source and saving or applying them
 */

case class ScheduleRunner(key: String,
                          name: String,
                          status: ManagerStatus,
                          conferenceReaders: List[DataSource[Conference]],
                          teamReaders: List[DataSource[Team]],
                          aliasReaders: List[DataSource[Alias]],
                          gameReaders: List[DataSource[Game]],
                          resultReaders: List[DataSource[Result]]) {
  val log = Logger.getLogger(this.getClass)
  require(StringUtils.isNotBlank(name) && validName(name))
  require(StringUtils.isNotBlank(key) && validKey(key))
  require(!conferenceReaders.isEmpty && !teamReaders.isEmpty && !aliasReaders.isEmpty && !gameReaders.isEmpty && !resultReaders.isEmpty)
  log.info("Initializing ")

  val sd = new ScheduleDao
  val cd = new ConferenceDao
  val td = new TeamDao
  val ad = new AliasDao
  val gd = new GameDao
  val rd = new ResultDao

  def load[T <: KeyedObject](schedule: Schedule, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    val list: List[Map[String, String]] = ds.load
    log.info("Loaded "+list.size+" observations.")
    for (data <- list; t <- ds.build(schedule, data)) {
      log.info("Saving "+t.key)
      dao.save(t)
    }
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


  def coldStartup: ScheduleRunner = {
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

  def warmStartup: ScheduleRunner = {
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


  def hotStartup: ScheduleRunner = {
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