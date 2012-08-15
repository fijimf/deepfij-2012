package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class NcaaComConferenceSource extends DataSource[Conference] {

  def load: List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def update(date: Date): List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def build(schedule: Schedule, data: Map[String, String]): Option[Conference] = {
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

  def loadAsOf(date: Date) = load

  def verify(t: Conference, u: Conference) = t.key == u.key && t.name == u.name
}


