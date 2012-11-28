package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.Result
import com.fijimf.deepfij.workflow.{Initializer, Exporter}
import com.fijimf.deepfij.workflow.datasource.ResultBuilder
import com.fijimf.deepfij.util.Logging

class ResultExporter(parms: Map[String, String]) extends Exporter[Result] with ResultBuilder with Initializer[Result] with Logging {

  def fileName = parms("fileName")

  def dataDir = parms("dataDir")

  def load = data

  def fromString(s: String): Map[String, String] = {
    s.split('|').toList match {
      case homeTeamName :: awayTeamName :: date :: homeTeamScore :: awayTeamScore :: tail => {
        Map("homeTeam" -> homeTeamName, "awayTeam" -> awayTeamName, "date" -> date, "homeScore" -> homeTeamScore, "awayScore" -> awayTeamScore)
      }
      case _ => Map.empty[String, String]
    }
  }

  def toString(r: Result): String = {
    r.game.homeTeam.key + "|" + r.game.awayTeam.key + "|" + yyyymmdd.format(r.game.date) + "|" + r.homeScore + "|" + r.awayScore
  }
}
