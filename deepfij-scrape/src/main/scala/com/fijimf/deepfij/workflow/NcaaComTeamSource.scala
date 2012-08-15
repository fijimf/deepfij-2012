package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class NcaaComTeamSource() extends DataSource[Team] {

  def load: List[Map[String, String]] = {
    NcaaTeamScraper.teamData
  }

  def update(date: Date): List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def build(schedule: Schedule, data: Map[String, String]): Option[Team] = {

    val Key = "key"
    val Name = "name"
    val ConferenceName = "conference"
    val LongName = "longName"
    val Nickname = "nickname"
    val PrimaryColor = "primaryColor"
    val SecondaryColor = "secondaryColor"
    val OfficialUrl = "officialUrl"
    val LogoUrl = "logo"

    for (
      key <- data.get("key");
      name <- data.get("name");
      conferenceName <- data.get("conference");
      conference <- schedule.conferenceByName.get(conferenceName);
      longName <- data.get("longName")
    ) yield {
      new Team( id= 0L,
        schedule = schedule,

        conference = conference,
        key = key,
        name = name,
        longName = longName,
        nickname = data("nickname"),
        primaryColor = data("primaryColor"),
        secondaryColor = data("secondaryColor"),
        officialUrl = data("officialUrl"),
        logo = data("logo"),
        updatedAt = new Date()
      )
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


