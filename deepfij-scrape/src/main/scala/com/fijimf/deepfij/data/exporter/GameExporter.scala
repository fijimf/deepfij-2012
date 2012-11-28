package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.Game
import com.fijimf.deepfij.workflow.{Initializer, Exporter}
import com.fijimf.deepfij.workflow.datasource.GameBuilder
import com.fijimf.deepfij.util.Logging

class GameExporter(parms: Map[String, String]) extends Exporter[Game] with GameBuilder with Initializer[Game] with Logging {

  def fileName = parms("fileName")

  def dataDir = parms("dataDir")

  def load = data

  def fromString(s: String): Map[String, String] = {
    s.split('|').toList match {
      case homeTeamName :: awayTeamName :: date :: isNeutralSite :: isConfTourn :: isNcaaTourn :: tail => {
        Map("homeTeam" -> homeTeamName, "awayTeam" -> awayTeamName, "date" -> date)
      }
      case _ => Map.empty[String, String]
    }
  }

  def toString(g: Game): String = {
    g.homeTeam.key + "|" + g.awayTeam.key + "|" + yyyymmdd.format(g.date) + "|" + g.isNeutralSite.toString + "|" + g.isConferenceTournament.toString + "|" + g.isNcaaTournament.toString
  }
}
