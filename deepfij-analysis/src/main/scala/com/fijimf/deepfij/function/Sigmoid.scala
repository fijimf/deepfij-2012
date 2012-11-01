package com.fijimf.deepfij.function

import scala.math._

object Sigmoid {
  def apply(t:Double):Double = {
    1.0/(1.0+exp(-t))
  }
}