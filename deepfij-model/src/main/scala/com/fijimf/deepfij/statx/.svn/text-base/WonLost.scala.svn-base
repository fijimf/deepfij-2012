package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Team, Schedule, Game}


object WonLost {
  def apply(s: Schedule)(d: Date): WonLost = {
    val gs = s.gameList.filter(g => g.date.before(d) && g.resultOpt.isDefined)

    new WonLost() {
      def won = gs.groupBy(_.winner.get).map {
        case (team, wins) => (team, wins.size.toDouble)
      }.toMap.withDefaultValue(0.0)

      def lost = gs.groupBy(_.loser.get).map {
        case (team, losses) => (team, losses.size.toDouble)
      }.toMap.withDefaultValue(0.0)

      def wp = new PartialFunction[Team, Double] {
        def apply(x: Team) = won(x) / (won(x) + lost(x))

        def isDefinedAt(x: Team) = won.isDefinedAt(x) && lost.isDefinedAt(x) && (won(x) + lost(x)) > 0.0
      }
    }
  }
}


trait WonLost {
  def won: PartialFunction[Team, Double]

  def lost: PartialFunction[Team, Double]

  def wp: PartialFunction[Team, Double]

  //  def winStreak: PartialFunction[Team, Double]
  //
  //  def lossStreak: PartialFunction[Team, Double]
}


