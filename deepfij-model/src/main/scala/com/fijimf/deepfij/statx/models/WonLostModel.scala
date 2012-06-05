package com.fijimf.deepfij.statx.models

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team}
import com.fijimf.deepfij.statx.{TeamModel, ModelContext, StatInfoImpl, SinglePassGameModel}

class WonLostModel extends SinglePassGameModel[Team] with TeamModel {
  val w: StatInfoImpl = StatInfoImpl("wins", higherIsBetter = true)
  val l: StatInfoImpl = StatInfoImpl("losses", higherIsBetter = false)
  val wp: StatInfoImpl = StatInfoImpl("wp", higherIsBetter = true)
  val ws: StatInfoImpl = StatInfoImpl("win-streak", higherIsBetter = true)
  val ls: StatInfoImpl = StatInfoImpl("loss-streak", higherIsBetter = false)

  case class WonLostRunning(wins: Double, losses: Double, winStreak: Double, lossStreak: Double)

  private[this] var runningTotals = Map.empty[Team, WonLostRunning]

  def statistics = List(w, l, wp, ws, ls)

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    gs.filter(g => (g.winner.isDefined && g.loser.isDefined)).map(g => {
      val winner = runningTotals.getOrElse(g.winner.get, WonLostRunning(0, 0, 0, 0))
      runningTotals += (g.winner.get -> WonLostRunning(winner.wins + 1, winner.losses, winner.winStreak + 1, 0))
      val loser = runningTotals.getOrElse(g.loser.get, WonLostRunning(0, 0, 0, 0))
      runningTotals += (g.loser.get -> WonLostRunning(loser.wins, loser.losses + 1, 0, loser.lossStreak + 1))
    })
    runningTotals.keys.foldLeft(ctx) {
      (ctx, team) => {
        val tot = runningTotals(team)
        ctx.update(w, d, team, tot.wins)
          .update(l, d, team, tot.losses)
          .update(wp, d, team, (tot.wins / (tot.wins + tot.losses)))
          .update(ws, d, team, tot.winStreak)
          .update(ls, d, team, tot.lossStreak)
      }
    }
  }
}
