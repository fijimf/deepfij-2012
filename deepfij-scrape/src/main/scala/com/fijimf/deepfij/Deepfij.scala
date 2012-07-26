package com.fijimf.deepfij

import xml.{Node, Elem, XML}
import java.io.InputStream
import io.Source


object Deepfij {
  def apply(config: String): Deepfij = {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream(config)
    //val s=Source.fromInputStream(stream).getLines().mkString("\n")
    Deepfij(XML.load(stream))
  }

  def apply(xml: Node): Deepfij = {
    new Deepfij(parseFactories(xml))
  }

  def parseFactories(n: Node): List[ScheduleFactory] = List.empty[ScheduleFactory]


}

class ScheduleFactory {
}

class Deepfij(fs: List[ScheduleFactory]) {

  //getName
  //getKey
  //Check Database for schedule
  //yes == verify name
  //no == create
  //
  // if
  //Conference reader => () => Map[String, String]
  //build conferences
  // compare to database


}

