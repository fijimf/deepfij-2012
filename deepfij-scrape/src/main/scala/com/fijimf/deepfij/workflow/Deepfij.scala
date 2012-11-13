package com.fijimf.deepfij.workflow

import xml.{NodeSeq, Node, XML}
import java.util.concurrent.{ScheduledExecutorService, Executors}
import org.apache.log4j.Logger


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
    val runners: NodeSeq = (n \ "schedule").toList
    require(runners.filter((node: Node) => node.attribute("primary").map(_.text == "true").getOrElse(false)).size == 1, "Exactly one schedule runner must be marked as primary")
    runners.map(RichScheduleRunner.fromNode(_)).toList
  }

  def main(args: Array[String]) {
    Deepfij(args.head)
  }
}
