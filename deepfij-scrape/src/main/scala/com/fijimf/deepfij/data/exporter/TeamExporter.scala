package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.{Schedule, Team}
import java.util.Date
import com.fijimf.deepfij.workflow.datasource.Exporter

class TeamExporter extends Exporter[Team] {


  def fromString(s: String): Map[String, String] = {
    def ident(m: Map[String, String]): Map[String, String] = m
    s.split('|').toList match {
      case key :: name :: conference :: longName :: logo :: nickname :: officialUrl :: primaryColor :: secondaryColor :: tail => {
        (ident _).andThen(addNotBlank(_, "nickname", nickname)).
          andThen(addNotBlank(_, "logo", logo)).
          andThen(addNotBlank(_, "officialUrl", officialUrl)).
          andThen(addNotBlank(_, "primaryColor", primaryColor)).
          andThen(addNotBlank(_, "secondaryColor", secondaryColor)).
          apply(Map("key" -> key, "name" -> name, "conference" -> conference, "longName" -> longName))
      }
    }
  }


  def toString(t: Team): String = {
    t.key + "|" + t.name + "|" + t.conference.key + "|" + t.longName + "|" + t.logoOpt.getOrElse("") + "|" + t.nicknameOpt.getOrElse("") + "|" + t.officialUrlOpt.getOrElse("") + "|" + t.primaryColorOpt.getOrElse("") + "|" + t.secondaryColorOpt.getOrElse("")
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

}
