package com.fijimf.deepfij.data.kenpom

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.Game
import java.util.Date
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}
import com.fijimf.deepfij.workflow.datasource.GameBuilder
import com.fijimf.deepfij.util.Logging

class KenPomGameSource(parms: Map[String, String]) extends Initializer[Game] with Updater[Game] with Verifier[Game] with GameBuilder with Logging {
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => yyyymmdd.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def isSame(t: Game, u: Game) =
    t.isConferenceTournament == u.isConferenceTournament &&
      t.isNcaaTournament == u.isNcaaTournament &&
      t.isNeutralSite == u.isNeutralSite
}
