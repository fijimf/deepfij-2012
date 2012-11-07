package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Schedule, Game}
import java.util.Date
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.data.ncaa.NcaaGameScraper


class NcaaComGameSource(parms: Map[String, String]) extends DataSource[Game] {
  val log = Logger.getLogger(this.getClass)

  val scraper = new NcaaGameScraper(Map.empty)
  val fmt = new SimpleDateFormat("yyyyMMdd")
  val startDate = fmt.parse(parms("startDate"))
  val endDate = fmt.parse(parms("endDate"))

  val dates: DateStream = DateStream(startDate, endDate)

  def load = for (d <- dates.toList;
                  resp <- scraper.loadDateGames(d).toList;
                  sc <- resp.scoreboard;
                  g <- sc.games) yield {
    Map("homeTeam" -> g.home.key, "awayTeam" -> g.away.key, "date" -> fmt.format(d))
  }


  def loadAsOf(date: Date) = for (resp <- scraper.loadDateGames(date).toList;
                                  sc <- resp.scoreboard;
                                  g <- sc.games) yield {
    Map("homeTeam" -> g.home.key, "awayTeam" -> g.away.key, "date" -> fmt.format(date))
  }


  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         date <- (data.get("date").map(fmt.parse(_)));
         homeTeam <- schedule.teamByKey.get(homeTeamName).orElse(teamsByName.get(homeTeamName)).orElse(schedule.aliasByKey.get(homeTeamName).map(_.team));
         awayTeam <- schedule.teamByKey.get(awayTeamName).orElse(teamsByName.get(awayTeamName)).orElse(schedule.aliasByKey.get(awayTeamName).map(_.team)))
    yield {
      new Game(schedule = schedule, homeTeam = homeTeam, awayTeam = awayTeam, date = date, updatedAt = new Date)
    }

  }

  def update(t: Game, data: Map[String, String]) = null

  def verify(t: Game, u: Game) = false
}
