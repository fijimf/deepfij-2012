package com.fijimf.deepfij.data.ncaa

import com.fijimf.deepfij.modelx.Result
import java.util.Date
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.{Logging, DateStream}
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}
import com.fijimf.deepfij.workflow.datasource.ResultBuilder

class NcaaComResultSource(parms: Map[String, String]) extends Initializer[Result] with Updater[Result] with Verifier[Result] with ResultBuilder with Logging {

  val scraper = new NcaaGameScraper(Map.empty)
  val startDate = yyyymmdd.parse(parms("startDate"))
  val endDate = yyyymmdd.parse(parms("endDate"))

  val dates: DateStream = DateStream(startDate, endDate)

  def load = for (d <- dates.toList;
                  resp <- scraper.loadDateGames(d).toList;
                  sc <- resp.scoreboard;
                  g <- sc.games) yield {
    val hs = g.home.scoreBreakdown.map(_.toInt).sum.toString
    val as = g.away.scoreBreakdown.map(_.toInt).sum.toString
    if (g.gameState.equalsIgnoreCase("final")) {
      Map("homeTeam" -> g.home.key, "homeScore" -> hs, "awayTeam" -> g.away.key, "awayScore" -> as, "date" -> yyyymmdd.format(d))
    } else {
      Map("homeTeam" -> g.home.key, "awayTeam" -> g.away.key, "date" -> yyyymmdd.format(d))
    }
  }

  def loadAsOf(date: Date) = load

  def isSame(t: Result, u: Result) = t.homeScore == u.homeScore && t.awayScore == u.awayScore
}
