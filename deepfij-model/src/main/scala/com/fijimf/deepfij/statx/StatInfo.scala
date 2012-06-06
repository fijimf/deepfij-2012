package com.fijimf.deepfij.statx

trait StatInfo {
  def statKey:String

  def name: String

  def format: String

  def higherIsBetter: Boolean
}

//case class StatInfoImpl(name: String, higherIsBetter: Boolean) extends StatInfo