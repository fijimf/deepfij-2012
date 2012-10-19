package com.fijimf.deepfij.workflow

import datasource.DataSource
import xml.{NodeSeq, Node, XML}
import com.fijimf.deepfij.modelx._
import scala.util.control.Exception._
import java.util.concurrent.{ScheduledExecutorService, Executors}
import org.apache.log4j.Logger


case class Deepfij(managers: List[ScheduleRunner]) {
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

  def parse(n: Node): List[ScheduleRunner] = {
    (n \ "schedule").map(parseMgr(_)).toList
  }

  def parseMgr(n: Node): ScheduleRunner = {
    val key = n.attribute("key").map(_.text).getOrElse("")
    val name = n.attribute("name").map(_.text).getOrElse("")
    ScheduleRunner(
      key = key,
      name = name,
      status = NotInitialized,
      conferenceReaders = parseReaders[Conference](n, "conferences"),
      aliasReaders = parseReaders[Alias](n, "aliases"),
      teamReaders = parseReaders[Team](n, "teams"),
      gameReaders = parseReaders[Game](n, "games"),
      resultReaders = parseReaders[Result](n, "results")
    )

  }

  def parseReaders[T <: KeyedObject](n: Node, t: String): List[DataSource[T]] = {
    val value = for (
      cn <- (n \ t);
      rn <- (cn \ "reader");
      tn <- rn.attribute("class")) yield {
      (rn \ "parameter") match {
        case NodeSeq.Empty => {
          catching(classOf[Exception]).opt(Class.forName(tn.text).newInstance().asInstanceOf[DataSource[T]])
        }
        case s: NodeSeq => {
          val parameters = (for (
            ss <- s;
            k <- ss.attribute("key");
            v <- ss.attribute("value")) yield {
            k.text -> v.text
          }).toMap
          println(parameters)
          catching(classOf[Exception]).opt(Class.forName(tn.text).getConstructor(classOf[Map[String, String]]).newInstance(parameters).asInstanceOf[DataSource[T]])
        }
      }
    }
    value.toList.flatten
  }

  def main(args: Array[String]) {
    val (deepfij, f, mode) = args.toList match {
      case config :: Nil => (Deepfij(config), (r: ScheduleRunner) => r.warmStartup, "warm")
      case config :: parm :: _ => {
        parm match {
          case "--hot-startup" => (Deepfij(config), (r: ScheduleRunner) => r.hotStartup, "hot")
          case "--cold-startup" => (Deepfij(config), (r: ScheduleRunner) => r.coldStartup, "cold")
          case _ => (Deepfij(config), (r: ScheduleRunner) => r.warmStartup, "warm")
        }
      }
      case Nil => throw new IllegalArgumentException("Config is required")
    }
    log.info("Loaded configuration from " + args(0))
    log.info("Start up mode is " + mode)
    deepfij.managers.flatMap(r => catching(classOf[IllegalStateException]).opt {
      log.info("Starting " + r.name + "(" + r.key + ")")
      f(r)
    })
  }

}
