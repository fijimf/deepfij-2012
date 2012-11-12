package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import datasource.DataSource
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._
import it.sauronsoftware.cron4j.Scheduler
import xml.{NodeSeq, Node}
import util.control.Exception._
import scala.Some
import collection.immutable.Seq

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

object Cron {
  val scheduler = new Scheduler
  scheduler.start()

  def scheduleJob(cron: String, f: () => Unit): String = {
    scheduler.schedule(cron, new Runnable() {
      def run() {
        f()
      }
    })
  }

  def shutdown() {
    scheduler.stop;
  }
}

case class DataManager[T <: KeyedObject](initializer: Option[DataSource[T]], updater: Option[DataSource[T]], exporter: Option[DataSource[T]], verfier: Option[DataSource[T]]) {

}

object RichScheduleRunner {
  def fromNode(n: Node): RichScheduleRunner = {
    val key = n.attribute("key").map(_.text).getOrElse("")
    val name = n.attribute("name").map(_.text).getOrElse("")
    val status = n.attribute("status").map(_.text) match {
      case Some("active") => ActiveSchedule
      case _ => HistoricalSchedule
    }
    val primary = n.attribute("primary").map(_.text).map(_.toBoolean).getOrElse(false)
    val startup = n.attribute("startup").map(_.text) match {
      case Some("hot") => HotStartup
      case Some("cold") => ColdStartup
      case _ => WarmStartup
    }

    RichScheduleRunner(
      key = key,
      name = name,
      status = status,
      primary = primary,
      startup = startup,
      conferenceMgr = parseManager[Conference](n, "conferences"),
      aliasMgr = parseManager[Alias](n, "aliases"),
      teamMgr = parseManager[Team](n, "teams"),
      gameMgr = parseManager[Game](n, "games"),
      resultMgr = parseManager[Result](n, "results")
    )
  }

  def parseManager[T <: KeyedObject](n: Node, t: String): DataManager[T] = {
    def getConstructor(dn: Node): Option[(String) => DataSource[T]] = {
      dn \ "parameter" match {
        case NodeSeq.Empty => {
          catching(classOf[Exception]).opt(Class.forName(_).newInstance().asInstanceOf[DataSource[T]])
        }
        case s: NodeSeq => {
          val parameters = (for (
            ss <- s;
            k <- ss.attribute("key");
            v <- ss.attribute("value")) yield {
            k.text -> v.text
          }).toMap
          catching(classOf[Exception]).opt(Class.forName(_).getConstructor(classOf[Map[String, String]]).newInstance(parameters).asInstanceOf[DataSource[T]])
        }
      }
    }
    val functions: Seq[(DataManager[T]) => DataManager[T]] = for (
      cn <- (n \ t);
      dn <- (cn \ "data");
      rn <- dn.attribute("role");
      tn <- dn.attribute("class")) yield {
      rn.text match {
        case "initializer" => (d: DataManager[T]) => d.copy(initializer = getConstructor(dn).map(_.apply(tn.text)))
        case "updater" => (d: DataManager[T]) => d.copy(updater = getConstructor(dn).map(_.apply(tn.text)))
        case "exporter" => (d: DataManager[T]) => d.copy(exporter = getConstructor(dn).map(_.apply(tn.text)))
        case "verifier" => (d: DataManager[T]) => d.copy(verfier = getConstructor(dn).map(_.apply(tn.text)))
      }
    }
    functions.foldLeft(DataManager[T](None, None, None, None))((dm, f) => f(dm))
  }

}

sealed trait ScheduleStatus

case object ActiveSchedule extends ScheduleStatus

case object HistoricalSchedule extends ScheduleStatus


case class RichScheduleRunner(key: String,
                              name: String,
                              status: ScheduleStatus,
                              primary: Boolean,
                              startup: StartupMode,
                              conferenceMgr: DataManager[Conference],
                              teamMgr: DataManager[Team],
                              aliasMgr: DataManager[Alias],
                              gameMgr: DataManager[Game],
                              resultMgr: DataManager[Result]) {

  val log = Logger.getLogger(this.getClass)
  require(StringUtils.isNotBlank(name) && validName(name))
  require(StringUtils.isNotBlank(key) && validKey(key))

  log.info("Initializing ")

  startup match {
    case ColdStartup => {
      val sd = new ScheduleDao
      sd.findByKey(key).map(s => {
        log.info("Dropping existing schedule with key %s".format(key))
        sd.delete(s.id)
      })
      log.info("Creating schedule with key %s".format(key))
      (sd.save _).
        andThen(s => conferenceMgr.initializer.map(i => load[Conference](s, i, new ConferenceDao())).getOrElse(s)).
        andThen(s => teamMgr.initializer.map(i => load[Team](s, i, new TeamDao())).getOrElse(s)).
        andThen(s => aliasMgr.initializer.map(i => load[Alias](s, i, new AliasDao())).getOrElse(s)).
        andThen(s => gameMgr.initializer.map(i => load[Game](s, i, new GameDao())).getOrElse(s)).
        andThen(s => resultMgr.initializer.map(i => load[Result](s, i, new ResultDao())).getOrElse(s)).
        apply(new Schedule(key = key, name = name))
      log.info("Initialization is complete.  Starting game/result monitor")
    }

    case WarmStartup => {
      val sd = new ScheduleDao
      val schedule = sd.findByKey(key) match {
        case Some(s) => s
        case None => sd.save(new Schedule(key = key, name = name))
      }
      (sd.save _).
        andThen(s => conferenceMgr.initializer.map(i => loadIfEmpty[Conference](s, _.conferenceList.isEmpty, i, new ConferenceDao())).getOrElse(s)).
        andThen(s => teamMgr.initializer.map(i => loadIfEmpty[Team](s, _.teamList.isEmpty, i, new TeamDao())).getOrElse(s)).
        andThen(s => aliasMgr.initializer.map(i => loadIfEmpty[Alias](s, _.aliasList.isEmpty, i, new AliasDao())).getOrElse(s)).
        andThen(s => gameMgr.initializer.map(i => load[Game](s, i, new GameDao())).getOrElse(s)).
        andThen(s => resultMgr.initializer.map(i => load[Result](s, i, new ResultDao())).getOrElse(s)).
        apply(schedule)
      log.info("Initialization is complete.  Starting game/result monitor")

    }
    case HotStartup => {

    }

  }

  private def load[T <: KeyedObject](schedule: Schedule, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    val list: List[Map[String, String]] = ds.load
    log.info("Loaded " + list.size + " observations.")
    dao.saveAll(for (data <- list; t <- ds.build(schedule, data)) yield {
      log.info("Saving " + t.key)
      t
    })
    new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
  }

  private def loadIfEmpty[T <: KeyedObject](schedule: Schedule, emptyTest: Schedule => Boolean, ds: DataSource[T], dao: BaseDao[T, _]): Schedule = {
    if (emptyTest(schedule)) {
      for (data <- ds.load; t <- ds.build(schedule, data)) {
        dao.save(t)
      }
      new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
    } else {
      schedule
    }
  }


  //  def warmStartup: RichScheduleRunner = {
  //    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
  //    val schedule = sd.findByKey(key) match {
  //      case Some(s) => s
  //      case None => sd.save(new Schedule(key = key, name = name))
  //    }
  //    (sd.save _).
  //      andThen(loadIfEmpty[Conference](_, _.conferenceList.isEmpty, conferenceReaders.head, cd)).
  //      andThen(loadIfEmpty[Team](_, _.teamList.isEmpty, teamReaders.head, td)).
  //      andThen(loadIfEmpty[Alias](_, _.aliasList.isEmpty, aliasReaders.head, ad)).
  //      andThen(loadIfEmpty[Game](_, _.gameList.isEmpty, gameReaders.head, gd)).
  //      apply(schedule)
  //    log.info("Initialization is complete.  Starting game/result monitor")
  //    copy(status = Running)
  //  }
  //
  //
  //  def hotStartup: RichScheduleRunner = {
  //    if (status != NotInitialized) throw new IllegalStateException("Cannot call startup on an itialized ScheduleRunner")
  //    val schedule = sd.findByKey(key) match {
  //      case Some(s) => s
  //      case None => throw new IllegalStateException("Unable to startup hot if schedule '%s' doesn't exist");
  //    }
  //    require(!schedule.conferenceList.isEmpty, "Hot startup failed.  No conferences are known.")
  //    require(!schedule.teamList.isEmpty, "Hot startup failed.  No teams are known.")
  //    log.info("Hot startup succeeded.  Starting game/result monitor")
  //
  //    copy(status = Running)
  //  }
  //
  //  def periodicCheck {
  //  }
  //
  //  def periodicUpdate {
  //  }


}