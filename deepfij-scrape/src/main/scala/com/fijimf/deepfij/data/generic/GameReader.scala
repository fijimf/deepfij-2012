package com.fijimf.deepfij.data.generic

import java.util.Date

trait GameReader {
  def aliasList: List[(String, String)]

  def gameList(date: Date): List[(String, Option[Int], String, Option[Int])]
}
