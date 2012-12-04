package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._
import xml.{NodeSeq, Node}
import util.control.Exception._
import scala.Some
import collection.immutable.Seq
import org.joda.time.DateMidnight
import java.util.Date


case class DataManager[T <: KeyedObject](initializer: Option[Initializer[T]],
                                         updater: Option[(Updater[T], Option[String])],
                                         exporter: Option[(Exporter[T], Option[String])],
                                         verifier: Option[(Verifier[T], Option[String])])

object RichScheduleRunner {
  def fromNode(n: Node): RichScheduleRunner = {
    val key = n.attribute("key").map(_.text).getOrElse("")
    val name = n.attribute("name").map(_.text).getOrElse("")
    val status = n.attribute("status").map(_.text) match {
      case Some("active") => ActiveSchedule
      case _ => HistoricalSchedule
    }
    val isPrimary = n.attribute("primary").map(_.text).map(_.toBoolean).getOrElse(false)
    val startup = n.attribute("startup").map(_.text) match {
      case Some("hot") => HotStartup
      case Some("cold") => ColdStartup
      case _ => throw new IllegalArgumentException("Bad startup parameter")
    }

    RichScheduleRunner(
      key = key,
      name = name,
      status = status,
      isPrimary = isPrimary,
      startup = startup,
      conferenceMgr = parseManager[Conference](n, "conferences"),
      aliasMgr = parseManager[Alias](n, "aliases"),
      teamMgr = parseManager[Team](n, "teams"),
      gameMgr = parseManager[Game](n, "games"),
      resultMgr = parseManager[Result](n, "results")
    )
  }

  def parseManager[T <: KeyedObject](n: Node, t: String): DataManager[T] = {
    def getConstructor(dn: Node): Option[(String) => Any] = {
      dn \ "parameter" match {
        case NodeSeq.Empty => {
          catching(classOf[Exception]).opt(Class.forName(_).newInstance())
        }
        case s: NodeSeq => {
          val parameters = (for (
            ss <- s;
            k <- ss.attribute("key");
            v <- ss.attribute("value")) yield {
            k.text -> v.text
          }).toMap
          catching(classOf[Exception]).opt(Class.forName(_).getConstructor(classOf[Map[String, String]]).newInstance(parameters))
        }
      }
    }
    val functions: Seq[(DataManager[T]) => DataManager[T]] = for (
      cn <- (n \ t);
      dn <- (cn \ "data");
      rn <- dn.attribute("role");
      tn <- dn.attribute("class")) yield {
      val ex = (dn \ "execution").headOption.flatMap(_.attribute("schedule")).flatMap(_.headOption).map(_.text)
      rn.text match {
        case "initializer" => (d: DataManager[T]) => d.copy(initializer = getConstructor(dn).map(_.apply(tn.text).asInstanceOf[Initializer[T]]))
        case "updater" => (d: DataManager[T]) => d.copy(updater = getConstructor(dn).map(f => (f.apply(tn.text).asInstanceOf[Updater[T]], ex)))
        case "exporter" => (d: DataManager[T]) => d.copy(exporter = getConstructor(dn).map(f => (f.apply(tn.text).asInstanceOf[Exporter[T]], ex)))
        case "verifier" => (d: DataManager[T]) => d.copy(verifier = getConstructor(dn).map(f => (f.apply(tn.text).asInstanceOf[Verifier[T]], ex)))
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
                              isPrimary: Boolean,
                              startup: StartupMode,
                              conferenceMgr: DataManager[Conference],
                              teamMgr: DataManager[Team],
                              aliasMgr: DataManager[Alias],
                              gameMgr: DataManager[Game],
                              resultMgr: DataManager[Result]) {

  val log = Logger.getLogger(this.getClass)
  require(StringUtils.isNotBlank(name) && validName(name))
  require(StringUtils.isNotBlank(key) && validKey(key))

  log.info("Initializing schedule %s(%s)".format(name, key))
  if (isPrimary) log.info("%s is the primary schedule of this instance".format(name))


  startup match {
    case ColdStartup => {
      log.info("COLD startup everything will be initialized.")
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
        apply(new Schedule(key = key, name = name, isPrimary = isPrimary))
      log.info("Initialization is complete.")
    }

    case HotStartup => {
      log.info("HOT startup no initialization, just start update jobs")
    }
  }

  initializeExporter(conferenceMgr, _.conferenceList)
  initializeExporter(aliasMgr, _.aliasList)
  initializeExporter(teamMgr, _.teamList)
  initializeExporter(gameMgr, _.gameList)
  initializeExporter(resultMgr, _.gameList.flatMap(_.resultOpt))

  if (status == ActiveSchedule) {
    initializeUpdater(gameMgr, new GameDao, _.gameList)
    initializeUpdater(resultMgr, new ResultDao, _.gameList.flatMap(_.resultOpt))
  }


  def initializeExporter[T <: KeyedObject](mgr: DataManager[T], f: (Schedule) => List[T]): Any = {
    mgr.exporter.foreach(exp => {
      val export = exp._1
      exp._2 match {
        case Some(sched) => {
          log.info("Setting schedule '%s' for exporter.".format(sched))
          Cron.scheduleJob(sched, () => {
            export.export(key, f)
          })
        }
        case None => {
          log.info("No schedule set for exporter.  Running once at startup")
          export.export(key, f)
        }
      }
    })
  }

  def initializeUpdater[T <: KeyedObject, ID](mgr: DataManager[T], dao: BaseDao[T, ID], f: (Schedule) => List[T]): Any = {
    mgr.updater.foreach(up => {
      val u = up._1
      up._2 match {
        case Some(sched) => {
          log.info("Setting schedule '%s' for updater.".format(sched))
          Cron.scheduleJob(sched, () => {
            val sd = new ScheduleDao
            sd.findByKey(key).map(s => {
              update[T, ID](s, u, dao, f)
            })
          })
        }
        case None => {
          log.info("No schedule set for updater.  Will not be called")
        }
      }
    })
  }

  private def load[T <: KeyedObject](schedule: Schedule, ds: Initializer[T], dao: BaseDao[T, _]): Schedule = {
    val list: List[Map[String, String]] = ds.load
    log.info("Loaded " + list.size + " observations.")
    dao.saveAll(for (data <- list; t <- ds.build(schedule, data)) yield {
      log.info("Saving " + t.key)
      t
    })
    new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
  }

  private def loadIfEmpty[T <: KeyedObject](schedule: Schedule, emptyTest: Schedule => Boolean, ds: Initializer[T], dao: BaseDao[T, _]): Schedule = {
    if (emptyTest(schedule)) {
      for (data <- ds.load; t <- ds.build(schedule, data)) {
        dao.save(t)
      }
      new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
    } else {
      schedule
    }
  }

  private def update[T <: KeyedObject, ID](schedule: Schedule, ds: Updater[T], dao: BaseDao[T, ID], f: Schedule => List[T], asOf: Date = new DateMidnight().toDate): Schedule = {
    val up: Map[String, T] = ds.loadAsOf(asOf).flatMap(d => ds.build(schedule, d)).map(x => x.key -> x).toMap
    val have: Map[String, T] = f(schedule).map(x => x.key -> x).toMap
    val updates = up.keySet.intersect(have.keySet).filter(k => !ds.isSame(up(k), have(k)))
    val inserts = up.keySet.diff(have.keySet)
    val deletes = have.keySet.diff(up.keySet)
    dao.deleteObjects((deletes ++ updates).toList.map(have(_)))
    dao.saveAll((updates ++ inserts).toList.map(up(_)))
    new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
  }
}