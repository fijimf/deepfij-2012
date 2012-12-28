package com.fijimf.deepfij.workflow

import xml.{NodeSeq, Node, XML}
import java.util.concurrent.{ScheduledExecutorService, Executors}
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx._


case class Deepfij(managers: List[RichScheduleRunner]) {
  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(4)
}

object Deepfij {
  val log = Logger.getLogger(this.getClass)

  def apply(config: String): Deepfij = {
    if (config.trim.startsWith("<deepfij>")) {
      Deepfij(XML.loadString(config))
    } else {
      val stream = classOf[Deepfij].getClassLoader.getResourceAsStream(config)
      Deepfij(XML.load(stream))
    }
  }

  def apply(xml: Node): Deepfij = {
    new Deepfij(parse(xml))
  }

  def parse(n: Node): List[RichScheduleRunner] = {
    case class Dpd[K <: KeyedObject](mgr: DataManager[K], f: (Schedule) => List[K])

    val schedNodes: NodeSeq = (n \ "schedule").toList
    require(schedNodes.filter((node: Node) => node.attribute("primary").map(_.text == "true").getOrElse(false)).size == 1, "Exactly one schedule runner must be marked as primary")
    val runners = schedNodes.map(n => {
      val r = RichScheduleRunner.fromNode(n)
      r.initializeSchedule()
      log.info("Initializing cron jobs")
      val cron = RichScheduleRunner.cronSchedule(n)
      cron.foreach {
        case (s: String, map: Map[String, String]) => {
          s match {
            case "conferences" => initializeCronJobs[Conference]("conferences", r, r.conferenceMgr, map, _.conferenceList, new ConferenceDao)
            case "aliases" => initializeCronJobs[Alias]("aliases", r, r.aliasMgr, map, _.aliasList, new AliasDao)
            case "teams" => initializeCronJobs[Team]("teams", r, r.teamMgr, map, _.teamList, new TeamDao)
            case "games" => initializeCronJobs[Game]("games", r, r.gameMgr, map, _.gameList, new GameDao)
            case "results" => initializeCronJobs[Result]("results", r, r.resultMgr, map, _.gameList.flatMap(_.resultOpt), new ResultDao)
          }
        }
      }
      log.info("Done initializing cron jobs")
      r
    })
    runners.toList
  }

  def initializeCronJobs[U <: KeyedObject](objKey: String, r: RichScheduleRunner, mgr: DataManager[U], map: Map[String, String], f: (Schedule) => List[U], dao: BaseDao[U, _]) {
    if (map.contains("exporter")) {
      val cronEntry = map("exporter")
      log.info("Running exporter for " + objKey)
      mgr.exporter.get.export(r.key, f)
      log.info("Adding exporter cron entry for" + objKey + " (" + cronEntry + ")")
      val id = Cron.scheduleJob(cronEntry, () => mgr.exporter.get.export(r.key, f))
      log.info("Scheduled job " + id)

    }
    if (map.contains("updater")) {
      val cronEntry = map("updater")
      log.info("Adding updater cron entry for" + objKey + " (" + cronEntry + ")")
      val id = Cron.scheduleJob(cronEntry, () => {
        log.info("Updating ")
        val (deletes, inserts) = mgr.updater.get.update(r.key, f)
        log.info("Deletes: \n" + deletes.map(_.key).mkString("\n"))
        log.info("Inserts: \n" + inserts.map(_.key).mkString("\n"))
        dao.deleteObjects(deletes)
        dao.saveAll(inserts)
      })
      log.info("Scheduled job " + id)
    }
  }

  def main(args: Array[String]) {
    Deepfij(args.head)
  }
}
