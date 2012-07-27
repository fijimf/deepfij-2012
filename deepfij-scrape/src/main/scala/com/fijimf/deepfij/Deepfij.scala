package com.fijimf.deepfij

import data.ncaa.json.Team
import modelx.{Alias, Conference}
import xml.{Node, XML}


object Deepfij {

  def apply(config: String): Deepfij = {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream(config)
    Deepfij(XML.load(stream))
  }

  def apply(xml: Node): Deepfij = {
    new Deepfij(parseFactories(xml))
  }

  def parseFactories(n: Node): List[ScheduleFactory] = List.empty[ScheduleFactory]


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

case class ScheduleFactory(key: String,
                           name: String,
                           confReaders: List[Reader[Conference]],
                           teamReaders: List[Reader[Team]],
                           aliasReaders: List[Reader[Alias]],
                           gameReaders: List[Reader[Team]],
                           resultReaders: List[Reader[Team]]) {

}

trait Reader[T] {
  def init: Int = 0
}

