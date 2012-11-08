package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Result, Schedule}
import java.util.Date
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.data.ncaa.NcaaGameScraper

class NcaaComResultSource(parms: Map[String, String]) extends DataSource[Result] {
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
    val hs = g.home.scoreBreakdown.map(_.toInt).sum.toString
    val as = g.away.scoreBreakdown.map(_.toInt).sum.toString
    Map("homeTeam" -> g.home.key, "homeScore" -> hs, "awayTeam" -> g.away.key, "awayScore" -> as, "date" -> fmt.format(d))
  }

  def loadAsOf(date: Date) = for (resp <- scraper.loadDateGames(date).toList;
                                  sc <- resp.scoreboard;
                                  g <- sc.games) yield {
    val hs = g.home.scoreBreakdown.map(_.toInt).sum.toString
    val as = g.away.scoreBreakdown.map(_.toInt).sum.toString
    Map("homeTeam" -> g.home.key, "homeScore" -> hs, "awayTeam" -> g.away.key, "awayScore" -> as, "date" -> fmt.format(date))
  }


  def build(schedule: Schedule, data: Map[String, String]) = {
    val teamsByName = schedule.teamList.map(t => (t.name -> t)).toMap
    for (homeTeamName <- data.get("homeTeam");
         awayTeamName <- data.get("awayTeam");
         homeScore <- data.get("homeScore").map(_.toInt);
         awayScore <- data.get("awayScore").map(_.toInt);
         date <- (data.get("date").map(fmt.parse(_)));
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
