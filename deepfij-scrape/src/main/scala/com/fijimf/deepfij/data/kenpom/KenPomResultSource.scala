package com.fijimf.deepfij.data.kenpom

import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.Result
import java.util.Date
import com.fijimf.deepfij.workflow.{Verifier, Initializer, Updater}
import com.fijimf.deepfij.workflow.datasource.ResultBuilder
import com.fijimf.deepfij.util.Logging

class KenPomResultSource(parms: Map[String, String]) extends Initializer[Result] with Updater[Result] with Verifier[Result] with ResultBuilder with Logging {
  val scraper = new KenPomScraper(parms("url"))

  def load = scraper.gameData.map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  def loadAsOf(date: Date) = scraper.gameData.filter(tup => yyyymmdd.parse(tup._1).before(date)).map(tup => Map("homeTeam" -> tup._2, "awayTeam" -> tup._4, "homeScore" -> tup._3, "awayScore" -> tup._5, "date" -> tup._1))

  def isSame(t: Result, u: Result) = t.homeScore == u.homeScore && t.awayScore == u.awayScore
}
