package com.fijimf.deepfij.util


object Util {
  def textKey(name: String): String = {
    name.replaceAll("[']", "").replaceAll("[^\\-a-zA-Z0-9 ]", " ").trim.toLowerCase.replaceAll(" +", "-")
  }
}
