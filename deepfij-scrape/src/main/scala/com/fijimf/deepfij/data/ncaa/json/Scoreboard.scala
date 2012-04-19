package com.fijimf.deepfij.data.ncaa.json

case class Scoreboard(day: String, games: List[Game]) {
  override def toString: String = {
    games.map(day + ": " + _.toString).mkString("\n")
  }
}
