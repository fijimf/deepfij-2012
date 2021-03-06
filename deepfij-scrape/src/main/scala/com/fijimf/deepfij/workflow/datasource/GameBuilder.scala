package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Schedule, Game}
import java.util.Date
import java.text.SimpleDateFormat
import com.fijimf.deepfij.workflow.Builder

trait GameBuilder extends Builder[Game] {
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         date <- (data.get("date").map(yyyymmdd.parse(_)));
         homeTeam <- schedule.teamByKey.get(homeTeamName).orElse(teamsByName.get(homeTeamName)).orElse(schedule.aliasByKey.get(homeTeamName).map(_.team));
         awayTeam <- schedule.teamByKey.get(awayTeamName).orElse(teamsByName.get(awayTeamName)).orElse(schedule.aliasByKey.get(awayTeamName).map(_.team)))
    yield {
      new Game(schedule = schedule, homeTeam = homeTeam, awayTeam = awayTeam, date = date, updatedAt = new Date)
    }
  }
}
