package com.fijimf.deepfij.workflow.datasource

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.{Schedule, Game}
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.util.Date
import java.text.SimpleDateFormat

class KenPomGameSource(parms: Map[String, String]) extends DataSource[Game] {
  val log = Logger.getLogger(this.getClass)
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => dfmt.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  val dfmt = new SimpleDateFormat("MM/dd/yyyy")

  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         date <- (data.get("date").map(dfmt.parse(_)));
         homeTeam <- schedule.teamByKey.get(homeTeamName).orElse(teamsByName.get(homeTeamName)).orElse(schedule.aliasByKey.get(homeTeamName).map(_.team));
         awayTeam <- schedule.teamByKey.get(awayTeamName).orElse(teamsByName.get(awayTeamName)).orElse(schedule.aliasByKey.get(awayTeamName).map(_.team)))
    yield {
      new Game(schedule = schedule, homeTeam = homeTeam, awayTeam = awayTeam, date = date, updatedAt = new Date)
    }

  }

  def update(t: Game, data: Map[String, String]) = null

  def verify(t: Game, u: Game) = false
}
