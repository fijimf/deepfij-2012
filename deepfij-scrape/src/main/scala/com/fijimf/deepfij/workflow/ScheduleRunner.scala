package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._

/**
 * So heres the deal abstracting out to the general case.
 *
 * We want to instantiate some aggregate type composed of other related components on startup
 * and (potentially) keep itself current.  The resources we have are
 * (1) Persistence we own, viz. a database;
 * (2) One or more primary sources which we don't own;
 * (3) Knowledge that the components of our Schedule aggregate can be identified by a unique, domain specific key;
 * (4) Ability to construct component objects given the primary source and (possibly partially constructed) aggregate;
 *
 *
 * Given those parameters we have three main use cases
 *
 * (1) Cold startup <- Rebuild everything; drop any existing data in the database
 * (2) Warm startup <- Static data is good; reload dynamic data
 * (3) Hot startup  <- Quick restart all data in the database is good and we can just start updater tasks
 */

case class ScheduleRunner(key: String,
                          name: String,
                          status: ManagerStatus,
                          conferenceReaders: List[DataSource[Conference]],
                          teamReaders: List[DataSource[Team]],
                          aliasReaders: List[DataSource[Alias]],
                          gameReaders: List[DataSource[Game]],
                          resultReaders: List[DataSource[Result]]) {
  require(StringUtils.isNotBlank(name) && validName(name))
  require(StringUtils.isNotBlank(key) && validKey(key))
  require(!conferenceReaders.isEmpty && !teamReaders.isEmpty && !aliasReaders.isEmpty && !gameReaders.isEmpty && !resultReaders.isEmpty)
  val log = Logger.getLogger(this.getClass)
  val sd = new ScheduleDao
  val cd = new ConferenceDao
  val td = new TeamDao
  val ad = new AliasDao


  def verifyTeams(schedule: Schedule, ds: DataSource[Team]) {

  }

  def verifyAliases(schedule: Schedule, ds: DataSource[Alias]) {
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

  def load[T <: KeyedObject](schedule: Schedule, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    for (data <- ds.load; t <- ds.build(schedule, data)) {
      dao.save(t)
    }
    sd.findByKey(schedule.key).getOrElse(schedule)
  }


  def coldStartup: ScheduleRunner = {
    log.info(" ***** COLD STARTUP ******")
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
        apply(new Schedule(key = key, name = name))
    conferenceReaders.tail.map(cr => verifyConferences(schedule, cr))
    teamReaders.tail.map(tr => verifyTeams(schedule, tr))
    aliasReaders.tail.map(ar => verifyAliases(schedule, ar))

    copy(status = Running)
  }

  def warmStartup: ScheduleRunner = {
    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => sd.save(new Schedule(key = key, name = name))
    }
    if (schedule.conferenceList.isEmpty) {
      load[Conference](schedule, conferenceReaders.head, cd)
    }
    conferenceReaders.tail.map(cr => verifyConferences(schedule, cr))

    copy(status = Running)
  }


  def hotStartup: ScheduleRunner = {
    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
    val schedule = sd.findByKey(key) match {
      case Some(s) => s
      case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doesn't exist");
    }
    copy(status = Running)
  }

  def periodicCheck {
  }

  def periodicUpdate {
  }


}