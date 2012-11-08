package com.fijimf.deepfij.workflow.datasource

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.{Schedule, Game}
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.util.Date
import java.text.SimpleDateFormat

class KenPomGameSource(parms: Map[String, String]) extends DataSource[Game] with GameBuilder {
  val log = Logger.getLogger(this.getClass)
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => dfmt.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "date" -> tup._1))

  val dfmt = new SimpleDateFormat("MM/dd/yyyy")

  def update(t: Game, data: Map[String, String]) = null

  def verify(t: Game, u: Game) = false
}
