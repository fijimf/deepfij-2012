package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx.{ConferenceDao, Schedule, Conference}
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class ConferenceSource(schedule: Schedule) {
  val conferenceDao = new ConferenceDao

  def load: List[Map[String, String]] = {
    NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet.map(n => (Map("key" -> nameToKey(n), "name" -> n))).toList
  }

  def update(date: Date): List[Map[String, String]] = {
    NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet.map(n => (Map("key" -> nameToKey(n), "name" -> n))).toList
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

  def verify(c: Conference, data: Map[String, String]): Boolean = {
    (for (n <- data.get("name")) yield {
      c.name == n
    }).getOrElse(false)
  }

}

