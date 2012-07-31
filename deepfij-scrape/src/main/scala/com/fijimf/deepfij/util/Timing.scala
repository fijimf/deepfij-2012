package com.fijimf.deepfij.util


object Timing {

  def timed[T](f: => T): (Long, T) = {
    val start = System.currentTimeMillis()
    val t: T = f
    ((System.currentTimeMillis() - start), t)
  }
}