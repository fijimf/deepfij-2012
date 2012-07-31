package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class ConferenceSource(schedule: Schedule) {
  val conferenceDao = new ConferenceDao

  def load: List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def update(date: Date): List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def fromKey(key: String): Option[Conference] = {
    conferenceDao.findByKey(schedule.key, key)
  }
}

class ConferenceBuilder(schedule: Schedule) {
  def build(data: Map[String, String]): Option[Conference] = {
    for (n <- data.get("name")) yield {
      new Conference(schedule = schedule, name = n, key = nameToKey(n))
    }
  }

  def update(c: Conference, data: Map[String, String]): Conference = {
    for (n <- data.get("name")) yield {
      c.name = n
    }
    c
  }

  def verify(c: Conference, data: Map[String, String]): Option[(Conference, Conference)] = {
    (for (n <- data.get("name")) yield {
      c.name == n
    }).getOrElse(false)
  }

}


trait Source[T >: KeyedObject] {

  def schedule: Schedule

  def load: List[Map[String, String]]

  def loadAsOf(date: Date): List[T]

  def fromKey(key: String): Option[T]

  def build(data: Map[String, String]): Option[T]

  def update(t: T, data: Map[String, String]): T

  def verify(t: T, data: Map[String, String]): Option[(T, T)]
}


object Tester {
  def main(args: Array[String]) {
    val sd = new ScheduleDao()
    val all: List[Schedule] = sd.findAll()
    all.foreach(s => println(s.key))

    val source: ConferenceSource = new ConferenceSource(sd.findByKey("test").get)

    val csd = source.load
    csd.foreach(ss => println(ss))
  }
}