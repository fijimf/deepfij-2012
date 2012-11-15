package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Result, Schedule}
import java.util.Date
import java.text.SimpleDateFormat
import com.fijimf.deepfij.workflow.Builder

trait ResultBuilder extends Builder[Result] {
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         homeScore <- data.get("homeScore").map(_.toInt);
         awayScore <- data.get("awayScore").map(_.toInt);
         date <- (data.get("date").map(yyyymmdd.parse(_)));
         homeTeam <- schedule.teamByKey.get(homeTeamName).orElse(teamsByName.get(homeTeamName)).orElse(schedule.aliasByKey.get(homeTeamName).map(_.team));
         awayTeam <- schedule.teamByKey.get(awayTeamName).orElse(teamsByName.get(awayTeamName)).orElse(schedule.aliasByKey.get(awayTeamName).map(_.team));
         game <- schedule.gameByKey.get(yyyymmdd.format(date) + ":" + homeTeam.key + ":" + awayTeam.key))
    yield {
      new Result(game = game, homeScore = homeScore, awayScore = awayScore, updatedAt = new Date)
    }
  }

}
