package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.Result
import java.util.Date
import org.apache.log4j.Logger
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.data.ncaa.NcaaGameScraper
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}

class NcaaComResultSource(parms: Map[String, String])  extends Initializer[Result] with Updater[Result] with Verifier[Result] with ResultBuilder {
  val log = Logger.getLogger(this.getClass)

  val scraper = new NcaaGameScraper(Map.empty)
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

  def verify(t: Result, u: Result) = false
}
