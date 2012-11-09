package com.fijimf.deepfij.workflow.datasource

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.{Result, Schedule}
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.util.Date
import java.text.SimpleDateFormat

class KenPomResultSource(parms: Map[String, String]) extends DataSource[Result] {
  val log = Logger.getLogger(this.getClass)
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => dfmt.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  val dfmt = new SimpleDateFormat("MM/dd/yyyy")
  val fmt = new SimpleDateFormat("yyyyMMdd")

  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         homeScore <- data.get("homeScore").map(_.toInt);
         awayScore <- data.get("awayScore").map(_.toInt);
         date <- (data.get("date").map(dfmt.parse(_)));
         homeTeam <- schedule.teamByKey.get(homeTeamName).orElse(teamsByName.get(homeTeamName)).orElse(schedule.aliasByKey.get(homeTeamName).map(_.team));
         awayTeam <- schedule.teamByKey.get(awayTeamName).orElse(teamsByName.get(awayTeamName)).orElse(schedule.aliasByKey.get(awayTeamName).map(_.team));
         game <- schedule.gameByKey.get(fmt.format(date) + ":" + homeTeam.key + ":" + awayTeam.key))
    yield {
      new Result(game = game, homeScore = homeScore, awayScore = awayScore, updatedAt = new Date)
    }
  }

  def update(t: Result, data: Map[String, String]) = null

  def verify(t: Result, u: Result) = false
}
