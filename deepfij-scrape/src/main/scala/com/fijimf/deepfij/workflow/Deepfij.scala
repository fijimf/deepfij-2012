package com.fijimf.deepfij.workflow

import xml.{Node, XML}
import com.fijimf.deepfij.modelx._
import java.util
import util.concurrent._

case class Deepfij(managers: List[ScheduleRunner]) {
  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(4)

  def coldStartup: Deepfij = {
    copy(managers = managers.map(_.coldStartup))
  }

  def hotStartup: Deepfij = {
    copy(managers = managers.map(_.hotStartup))
  }

  def warmStartup: Deepfij = {
    copy(managers = managers.map(_.warmStartup))
  }
}

object Deepfij {

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
    val value = for (cn <- (n \ t); rn <- (cn \ "reader"); tn <- rn.attribute("class")) yield {
      Class.forName(tn.text).newInstance().asInstanceOf[DataSource[T]]
    }
    value.toList
  }


}
