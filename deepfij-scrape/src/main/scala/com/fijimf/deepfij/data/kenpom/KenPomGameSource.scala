package com.fijimf.deepfij.data.kenpom

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.Game
import java.util.Date
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}
import com.fijimf.deepfij.workflow.datasource.GameBuilder

class KenPomGameSource(parms: Map[String, String]) extends Initializer[Game] with Updater[Game] with Verifier[Game] with GameBuilder {
  val log = Logger.getLogger(this.getClass)
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => yyyymmdd.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def verify(t: Game, u: Game) = false
}
