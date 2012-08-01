package com.fijimf.deepfij.workflow

import xml.{Node, XML}

case class Deepfij(managers: List[ScheduleManager]) {


}

object Deepfij {

  def apply(config: String): Deepfij = {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream(config)
    Deepfij(XML.load(stream))
  }

  def apply(xml: Node): Deepfij = {
    new Deepfij(parse(xml))
  }

  def parse(n: Node): List[ScheduleManager] = {

  }


}
