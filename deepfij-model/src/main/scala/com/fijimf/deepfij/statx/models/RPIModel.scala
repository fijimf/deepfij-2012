package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx.{Game, MetaStat, Team}
import java.util.Date


class RPIModel extends StatisticalModel[Team] with TeamModel {

  def name = null

  def key = null

  val w = new MetaStat(name = "Wins", statKey = "wins", format = "%3.0f", higherIsBetter = true)
  val l = new MetaStat(name = "Losses", statKey = "losses", format = "%3.0f", higherIsBetter = false)
  val wp = new MetaStat(name = "Winning Pct.", statKey = "wp", format = "%3.0f", higherIsBetter = true)
  val ow = new MetaStat(name = "Opp Wins", statKey = "opp-wins", format = "%3.0f", higherIsBetter = true)
  val ol = new MetaStat(name = "Opp Losses", statKey = "opp-losses", format = "%3.0f", higherIsBetter = false)
  val owp = new MetaStat(name = "Opp Winning Pct.", statKey = "opp-wp", format = "%3.0f", higherIsBetter = true)
  val oow = new MetaStat(name = "Opp Opp Wins", statKey = "opp-opp-wins", format = "%3.0f", higherIsBetter = true)
  val ool = new MetaStat(name = "Opp Opp Losses", statKey = "opp-opp-losses", format = "%3.0f", higherIsBetter = false)
  val oowp = new MetaStat(name = "Opp Opp Winning Pct.", statKey = "opp-opp-wp", format = "%3.0f", higherIsBetter = true)

  case class RPIRunning(wins: Double, losses: Double, oppWins: Double, oppLosses: Double, oppOppWins: Double, oppOppLosses: Double)

  private[this] var runningTotals = Map.empty[Team, RPIRunning]

  def statistics = List(w, l, wp, ow, ol, owp, oow, ool, oowp)

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    gs.filter(g => (g.winner.isDefined && g.loser.isDefined)).map(g => {

    })

  }
}
