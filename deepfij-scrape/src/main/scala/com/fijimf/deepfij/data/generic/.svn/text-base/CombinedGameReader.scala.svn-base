package com.fijimf.deepfij.data.generic

import java.util.Date

case class CombinedGameReader(primary: GameReader, secondary: GameReader) extends GameReader {
  override def aliasList: List[(String, String)] = {
    val primaryMap = primary.aliasList.toMap
    val secondaryMap = secondary.aliasList.toMap

    (secondaryMap ++ primaryMap).toList
  }

  def gameList(date: Date) = {
    secondary.gameList(date) ++ primary.gameList(date)
  }
}