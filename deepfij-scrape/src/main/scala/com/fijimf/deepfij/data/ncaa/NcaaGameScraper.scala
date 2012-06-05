package com.fijimf.deepfij.data.ncaa

import java.util.Date
import java.text.SimpleDateFormat
import dispatch.{url, Http}
import com.fijimf.deepfij.data.generic.GameReader
import json.{Team, GameResponse}
import com.codahale.jerkson.Json._

class NcaaGameScraper(teams: Map[String, String]) extends GameReader {


  val names = teams.values.toList
  override def aliasList = teams.toList

  def loadDateGames(d: Date): Option[GameResponse] = {
    val year = new SimpleDateFormat("yyyy").format(d)
    val month = new SimpleDateFormat("MM").format(d)
    val day = new SimpleDateFormat("dd").format(d)

    val req = "http://data.ncaa.com/jsonp/scoreboard/basketball-men/d1/" + year + "/" + month + "/" + day + "/scoreboard.html"

    val h = new Http()
    def parseGameResponse(s: String): Option[GameResponse] = {
      val j = s.replaceFirst("""^callbackWrapper\(\{""", "{").replaceFirst("""\}\)$""", "}").replaceAll(""",\s+,""", ",")
      try {
        Some(parse[GameResponse](j))
      } catch {
        case e: Exception => {
          println(d + "  " + e.getMessage)
          e.printStackTrace()
          println(j)
          None
        }
      }
    }
    try {
      h(url(req) >- parseGameResponse _)
    } catch {
      case e: Exception => {
        println(d + ": Caught " + e.getMessage)
        None
      }
    } finally {
      h.shutdown()
    }
  }

  def dirtyTeamList = teams.values.map((_, 1.0)).toList



  def gameList(date: Date): List[(String, Option[Int], String, Option[Int])] = {
    (for (gr <- loadDateGames(date).toList;
          sb <- gr.scoreboard;
          g <- sb.games) yield {
      val (homeTeam, homeScore) = teamData(g.home)
      val (awayTeam, awayScore) = teamData(g.away)
      (homeTeam, homeScore, awayTeam, awayScore)
    }).toList
  }

  def teamData(t: Team):(String,  Option[Int]) ={
    val name = teams.getOrElse(t.key, t.name)
    val score = t.scoreBreakdown match {
      case Nil => None
      case xs => Some(xs.map(_.toInt).sum)
    }
    (name, score)
  }
}
