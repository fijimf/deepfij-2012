package com.fijimf.deepfij.data.ncaa.json

case class GameResponse(scoreboard: List[Scoreboard]) {
  override def toString: String = {
    scoreboard.mkString("\n")
  }
}
