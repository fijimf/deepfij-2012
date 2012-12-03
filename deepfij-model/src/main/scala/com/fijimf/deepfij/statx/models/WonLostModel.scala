package com.fijimf.deepfij.statx.models

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, MetaStat}
import com.fijimf.deepfij.statx.{TeamModel, ModelContext, SinglePassGameModel}

class WonLostModel extends SinglePassGameModel[Team] with TeamModel {
  val w = new MetaStat(name = "Wins", statKey = "wins", format = "%3.0f", higherIsBetter = true)
  val l = new MetaStat(name = "Losses", statKey = "losses", format = "%3.0f", higherIsBetter = false)
  val wp = new MetaStat(name = "Winning Pct.", statKey = "wp", format = "%3.0f", higherIsBetter = true)
  val ws = new MetaStat(name = "Win Streak", statKey = "win-streak", format = "%3.0f", higherIsBetter = true)
  val ls = new MetaStat(name = "Loss Streak", statKey = "loss-streak", format = "%3.0f", higherIsBetter = false)

  case class WonLostRunning(wins: Double = 0, losses: Double = 0, winStreak: Double = 0, lossStreak: Double = 0) {
    def withWin: WonLostRunning = copy(wins = wins + 1, winStreak = winStreak + 1, lossStreak = 0)

    def withLoss: WonLostRunning = copy(losses = losses + 1, winStreak = 0, lossStreak = lossStreak + 1)
  }

  private[this] var runningTotals = Map.empty[Team, WonLostRunning].withDefaultValue(WonLostRunning())

  def statistics = List(w, l, wp, ws, ls)

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    for (g <- gs; r <- g.resultOpt; wt <- g.winner; lt <- g.loser) {
      runningTotals += (wt -> runningTotals(wt).withWin)
      runningTotals += (lt -> runningTotals(lt).withLoss)
    }
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
