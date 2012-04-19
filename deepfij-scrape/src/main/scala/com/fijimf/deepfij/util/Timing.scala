package com.fijimf.deepfij.util


object Timing {

  def elapsedTime(f:  => Unit): Double = {
    val start = System.currentTimeMillis()
    f
    (System.currentTimeMillis() - start).toDouble / 1000.0
  }
}