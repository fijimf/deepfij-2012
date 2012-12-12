package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Team, Schedule}
import java.util.Date
import com.fijimf.deepfij.workflow.Builder

trait TeamBuilder extends Builder[Team] {
  def build(schedule: Schedule, data: Map[String, String]): Option[Team] = {
    for (
      key <- data.get("key");
      name <- data.get("name");
      conferenceName <- data.get("conference");
      conference <- schedule.conferenceByName.get(conferenceName).orElse(schedule.conferenceByKey.get(conferenceName));
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
