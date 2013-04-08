package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.Game

trait FeatureMapper {
  def dim: Int = features.size

  def features: List[String]

  def f(g: Game): Option[Array[Double]]
}
