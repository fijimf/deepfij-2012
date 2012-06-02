package com.fijimf.deepfij.statx

trait StatInfo {
  def name: String

  def higherIsBetter: Boolean
}

case class StatInfoImpl(name: String, higherIsBetter: Boolean) extends StatInfo