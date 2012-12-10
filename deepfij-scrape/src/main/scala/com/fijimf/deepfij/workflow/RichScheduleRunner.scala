package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx._
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.Validation._
import xml.{NodeSeq, Node}
import util.control.Exception._
import scala.Some
import org.joda.time.DateMidnight
import java.util.Date

object DataManager {
  def fromNode[T <: KeyedObject](n: Node): DataManager[T] = {
    val functions = for (dataNode <- (n \ "data");
                         role <- dataNode.attribute("role").flatMap(_.headOption);
                         className <- dataNode.attribute("class").flatMap(_.headOption)) yield {
      dataOperatorCreator[T](role, dataNode, className)
    }
    functions.foldLeft(DataManager[T]())((dm, f) => f(dm))
  }


  def dataOperatorCreator[T <: KeyedObject](role: Node, dataNode: Node, className: Node): (DataManager[T]) => DataManager[T] = {
    role.text match {
      case "initializer" => (d: DataManager[T]) => d.copy(initializer = getConstructor(dataNode).map(_.apply(className.text).asInstanceOf[Initializer[T]]))
      case "updater" => (d: DataManager[T]) => d.copy(updater = getConstructor(dataNode).map(_.apply(className.text).asInstanceOf[Updater[T]]))
      case "exporter" => (d: DataManager[T]) => d.copy(exporter = getConstructor(dataNode).map(_.apply(className.text).asInstanceOf[Exporter[T]]))
      case "verifier" => (d: DataManager[T]) => d.copy(verifier = getConstructor(dataNode).map(_.apply(className.text).asInstanceOf[Verifier[T]]))
    }
  }

  def getConstructor(dn: Node): Option[(String) => Any] = {
    dn \ "parameter" match {
      case NodeSeq.Empty => noArgConstructor
      case s: NodeSeq => parameterMapConstructor(s)
    }
  }

  def parameterMapConstructor(s: NodeSeq): Option[(String) => _] = {
    val keyValues = for (ss <- s; k <- ss.attribute("key"); v <- ss.attribute("value")) yield {
      k.text -> v.text
    }
    catching(classOf[Exception]).opt((s: String) => Class.forName(s).getConstructor(classOf[Map[String, String]]).newInstance(keyValues.toMap))
  }

  def noArgConstructor: Option[(String) => _] = {
    catching(classOf[Exception]).opt((s: String) => Class.forName(s).newInstance())
  }
}

case class DataManager[T <: KeyedObject](initializer: Option[Initializer[T]] = None,
                                         updater: Option[Updater[T]] = None,
                                         exporter: Option[Exporter[T]] = None,
                                         verifier: Option[Verifier[T]] = None)

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
      conferenceMgr = DataManager.fromNode[Conference]((n \ "conferences").head),
      aliasMgr = DataManager.fromNode[Alias]((n \ "aliases").head),
      teamMgr = DataManager.fromNode[Team]((n \ "teams").head),
      gameMgr = DataManager.fromNode[Game]((n \ "games").head),
      resultMgr = DataManager.fromNode[Result]((n \ "results").head)
    )
  }

  def cronSchedule(n: Node): Map[String, Map[String, String]] = {
    (for (d <- List("conferences", "aliases", "teams", "games", "results");
          dn <- (n \ d)) yield {
      d -> (for (r <- List("exporter", "updater", "verifier");
                 rn <- (dn \ r);
                 s <- rn.attribute("schedule").flatMap(_.headOption)) yield {
        r -> s.text
      }).toMap
    }).toMap
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
  log.info("Schedule %s status is %s".format(key, status))
  if (isPrimary)
    log.info("%s is the primary schedule of this instance".format(key))
  else
    log.info("%s is NOT the primary schedule of this instance".format(key))

  def initializeSchedule() {
    val sd = new ScheduleDao
    val schedule: Schedule = startup match {
      case ColdStartup => {
        log.info("COLD startup everything will be initialized.")
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
      }

      case HotStartup => {
        log.info("HOT startup no initialization, just start update jobs")
        sd.findByKey(key) match {
          case Some(s) => s
          case None => throw new IllegalArgumentException("Schedule not found on hot startup")
        }
      }
    }
    log.info("Schedule initialization complete")
    schedule
  }

  initializeSchedule()

  //  initializeExporter(conferenceMgr, _.conferenceList)
  //  initializeExporter(aliasMgr, _.aliasList)
  //  initializeExporter(teamMgr, _.teamList)
  //  initializeExporter(gameMgr, _.gameList)
  //  initializeExporter(resultMgr, _.gameList.flatMap(_.resultOpt))
  //
  //  if (status == ActiveSchedule) {
  //    initializeUpdater(gameMgr, new GameDao, _.gameList)
  //    initializeUpdater(resultMgr, new ResultDao, _.gameList.flatMap(_.resultOpt))
  //  }


  private def load[T <: KeyedObject](schedule: Schedule, ds: Initializer[T], dao: BaseDao[T, _]): Schedule = {
    val list: List[Map[String, String]] = ds.load
    log.info("Loaded " + list.size + " observations.")
    dao.saveAll(for (data <- list; t <- ds.build(schedule, data)) yield {
      log.info("Saving " + t.key)
      t
    })
    new ScheduleDao().findByKey(schedule.key).getOrElse(schedule)
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