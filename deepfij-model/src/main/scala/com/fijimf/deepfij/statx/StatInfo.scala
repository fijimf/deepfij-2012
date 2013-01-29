package com.fijimf.deepfij.statx

trait StatInfo {
  def modelKey: String

  def modelName: String

  def statKey: String

  def name: String

  def format: String

  def higherIsBetter: Boolean
}