package com.fijimf.deepfij.server.controller.api

import com.fijimf.deepfij.server.controller.Controller
import com.fijimf.deepfij.modelx.Result
import java.text.SimpleDateFormat
import com.codahale.jerkson.Json
import org.joda.time.{DateMidnight, DateTime, Days}


trait StatsController {
  this: Controller =>

  case class GameNode(opponentName: String, opponentKey: String,opponentLogo:String, score: Int, oppScore: Int, dateStr: String, month: String, julDate: Int)

  case class TeamNode(name: String, key: String, logo: String, games: List[GameNode])

  case class RootNode(name: String, teams: List[TeamNode])

  val startDate = new DateMidnight(1980, 1, 1)
  val monFmt = new SimpleDateFormat("MMM")
  get("/api/games") {
    contentType = "application/json"
    Json.generate(RootNode(schedule.name, schedule.teamList.map(t => {
      TeamNode(t.name, t.key, t.logo, t.games.filter(_.resultOpt.isDefined).sortBy(_.date).map(g => {
        val opp = (if (g.isWin(t)) g.loser else g.winner).get
        val res: Result = g.result
        val (score, oppScore) = if (g.homeTeam == t) (res.homeScore, res.awayScore) else (res.awayScore, res.homeScore)
        GameNode(opp.name, opp.key, opp.logo, score, oppScore, yyyymmdd.format(g.date), monFmt.format(g.date), Days.daysBetween(startDate, new DateTime(g.date.getTime)).getDays)
      }))
    }))
    )
  }

  get("/games") {
    contentType = "text/html"
    templateEngine.layout("pages/gamegraphs.mustache", attributes())
  }
}
