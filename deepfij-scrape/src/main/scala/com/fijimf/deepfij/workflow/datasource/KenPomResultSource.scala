package com.fijimf.deepfij.workflow.datasource

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.Result
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.util.Date
import java.text.SimpleDateFormat
import com.fijimf.deepfij.workflow.{Verifier, Initializer, Updater}

class KenPomResultSource(parms: Map[String, String]) extends Initializer[Result] with Updater[Result] with Verifier[Result] with ResultBuilder {
  val log = Logger.getLogger(this.getClass)
  val scraper = new KenPomScraper(parms("url"))
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => yyyymmdd.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  def verify(t: Result, u: Result) = false
}
