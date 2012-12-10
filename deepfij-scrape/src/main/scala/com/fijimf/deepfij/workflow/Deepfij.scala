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
      val cron = RichScheduleRunner.cronSchedule(n)
      cron.foreach {
        case (s: String, map: Map[String, String]) => {
          s match {
            case "conferences" => initializeCronJobs[Conference](r.conferenceMgr, map)
            case "aliases" => initializeCronJobs[Alias](r.aliasMgr, map)
            case "teams" => initializeCronJobs[Team](r.teamMgr, map)
            case "games" => initializeCronJobs[Game](r.gameMgr, map)
            case "results" => initializeCronJobs[Result](r.resultMgr, map)
          }
        }
      }
      r
    })
    runners.toList
  }

  def initializeCronJobs[U <: KeyedObject](mgr: DataManager[U], map: Map[String, String]) {

  }

  def main(args: Array[String]) {
    Deepfij(args.head)
  }
}
