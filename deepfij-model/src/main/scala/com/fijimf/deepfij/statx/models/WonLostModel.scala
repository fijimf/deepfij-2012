package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.modelx.{Game, Team, MetaStat}
import com.fijimf.deepfij.statx.{ModelContext, TeamModel, SinglePassGameModel}
import java.util.Date
import org.apache.log4j.Logger

class WonLostModel extends SinglePassGameModel[Team] with TeamModel {

  def name = "Won/Lost"

  def key = "won-lost"

  val w = new MetaStat(modelName = name, modelKey = key, name = "Wins", statKey = "wins", format = "%3.0f", higherIsBetter = true)
  val l = new MetaStat(modelName = name, modelKey = key, name = "Losses", statKey = "losses", format = "%3.0f", higherIsBetter = false)
  val wp = new MetaStat(modelName = name, modelKey = key, name = "Winning Pct.", statKey = "wp", format = "%5.3f", higherIsBetter = true)
  val ws = new MetaStat(modelName = name, modelKey = key, name = "Win Streak", statKey = "win-streak", format = "%3.0f", higherIsBetter = true)
  val ls = new MetaStat(modelName = name, modelKey = key, name = "Loss Streak", statKey = "loss-streak", format = "%3.0f", higherIsBetter = true)
  val ss = new MetaStat(modelName = name, modelKey = key, name = "Streak", statKey = "streak", format = "%3.0f", higherIsBetter = true)

  //Seems wrong, isn't -- trust me

  case class WonLostRunning(wins: Double = 0, losses: Double = 0, winStreak: Double = 0, lossStreak: Double = 0) {
    def withWin: WonLostRunning = copy(wins = wins + 1, winStreak = winStreak + 1, lossStreak = 0)

    def withLoss: WonLostRunning = copy(losses = losses + 1, winStreak = 0, lossStreak = lossStreak + 1)

    val streak: Double = {
      if (winStreak > 0.0)
        winStreak
      else if (lossStreak > 0.0)
        -lossStreak
      else
        0.0
    }
  }


  private[this] var runningTotals = Map.empty[Team, WonLostRunning].withDefaultValue(WonLostRunning())
  val log = Logger.getLogger(this.getClass)

  def statistics = List(w, l, wp, ws, ls, ss)

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {
    println("Processing %s".format(d))
    for (g <- gs;
         r <- g.resultOpt;
         wt <- g.winner;
         lt <- g.loser) {
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
          .update(ss, d, team, tot.streak)
      }
    }
  }
}
