package com.fijimf.deepfij.workflow.datasource

import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class NcaaComTeamSource() extends DataSource[Team] {

  def load: List[Map[String, String]] = {
    NcaaTeamScraper.teamData
  }

  def update(date: Date): List[Map[String, String]] = {
    List.empty
  }

  def build(schedule: Schedule, data: Map[String, String]): Option[Team] = {
   for (
      key <- data.get("key");
      name <- data.get("name");
      conferenceName <- data.get("conference");
      conference <- schedule.conferenceByName.get(conferenceName);
      longName <- data.get("longName")
    ) yield {
      new Team(id = 0L,
        schedule = schedule,
        conference = conference,
        key = key,
        name = name,
        longName = longName,
        nickname = data.get("nickname").orNull,
        primaryColor = data.get("primaryColor").orNull,
        secondaryColor = data.get("secondaryColor").orNull,
        officialUrl = data.get("officialUrl").orNull,
        logo = data.get("logo").orNull,
        updatedAt = new Date()
      )
    }
  }

  def update(c: Team, data: Map[String, String]): Team = {
    for (n <- data.get("name")) yield {
      c.name = n
    }
    c
  }

  def loadAsOf(date: Date) = load

  def verify(t: Team, u: Team) = t.key == u.key && t.name == u.name
}


