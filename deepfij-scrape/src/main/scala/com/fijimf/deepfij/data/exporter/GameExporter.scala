package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.{Game, Team}
import com.fijimf.deepfij.workflow.Exporter
import com.fijimf.deepfij.workflow.datasource.TeamBuilder
import com.fijimf.deepfij.util.Logging

class GameExporter(parms: Map[String, String]) extends Exporter[Team] with TeamBuilder with Logging {


  def fromString(s: String): Map[String, String] = {
    s.split('|').toList match {
      case homeTeamName :: awayTeamName :: date :: isNeutralSite :: isConfTourn :: isNcaaTourn :: tail => {
        Map("homeTeam" -> homeTeamName, "awayTeam" -> awayTeamName, "date" -> date)
      }
      case _ => Map.empty[String, String]
    }
  }

  def toString(g: Game): String = {
    g.homeTeam.key + "|" + g.awayTeam.key + "|" + yyyymmdd.format(g.date) + "|" + g.isNeutralSite.getOrElse(false).toString + "|" + g.isConferenceTournament.getOrElse(false).toString + "|" + g.isNcaaTournament.getOrElse(false).toString
  }
}
