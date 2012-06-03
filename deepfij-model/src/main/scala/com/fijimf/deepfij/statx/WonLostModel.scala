package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, Schedule}

abstract class WonLostModel extends SinglePassGameModel[Team] {
  val w: StatInfo = StatInfoImpl("wins", higherIsBetter = true)
  val l: StatInfo = StatInfoImpl("losses", higherIsBetter = false)
  val wp: StatInfo = StatInfoImpl("wp", higherIsBetter = true)
  val ws: StatInfo = StatInfoImpl("win-streak", higherIsBetter = true)
  val ls: StatInfo = StatInfoImpl("loss-streak", higherIsBetter = false)

  def keys = {
    List(w, l, wp, ws, ls)
  }

  def valueKeys(s: Schedule) = s.teamList.sortBy(_.name)

  def valueStartDate(s: Schedule) = s.gameList.minBy(_.date).date

  def valueEndDate(s: Schedule) = s.gameList.maxBy(_.date).date

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[Team]) = {

  }
}
