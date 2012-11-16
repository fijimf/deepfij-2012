package com.fijimf.deepfij.data.ncaa

import com.fijimf.deepfij.modelx.Game
import java.util.Date
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import com.fijimf.deepfij.util.DateStream
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}
import com.fijimf.deepfij.workflow.datasource.GameBuilder
import org.apache.commons.lang.time.DateUtils


class NcaaComGameSource(parms: Map[String, String])extends Initializer[Game] with Updater[Game] with Verifier[Game] with GameBuilder {
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


  def loadAsOf(date: Date) = for (d <- dates.toList.filter(_.before(DateUtils.addWeeks(date,1)));
                    resp <- scraper.loadDateGames(d).toList;
                    sc <- resp.scoreboard;
                    g <- sc.games) yield {
      Map("homeTeam" -> g.home.key, "awayTeam" -> g.away.key, "date" -> fmt.format(d))
    }

  def isSame(t: Game, u: Game) =
       t.isConferenceTournament == u.isConferenceTournament &&
       t.isNcaaTournament == u.isNcaaTournament &&
       t.isNeutralSite == u.isNeutralSite
}
