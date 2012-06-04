package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, Schedule}

class WonLostModel extends SinglePassGameModel[Team] {
  val w: StatInfoImpl = StatInfoImpl("wins", higherIsBetter = true)
  val l: StatInfoImpl = StatInfoImpl("losses", higherIsBetter = false)
  val wp: StatInfoImpl = StatInfoImpl("wp", higherIsBetter = true)
  val ws: StatInfoImpl = StatInfoImpl("win-streak", higherIsBetter = true)
  val ls: StatInfoImpl = StatInfoImpl("loss-streak", higherIsBetter = false)

  case class WonLostRunning(wins: Double, losses: Double, winStreak: Double, lossStreak: Double)

  private[this] var runningTotals = Map.empty[Team, WonLostRunning]

  def statistics = List(w, l, wp, ws, ls)

  def scheduleKeys(s: Schedule) = s.teamList.sortBy(_.name)

  def scheduleStartDate(s: Schedule) = s.gameList.minBy(_.date).date

  def scheduleEndDate(s: Schedule) = s.gameList.maxBy(_.date).date

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
