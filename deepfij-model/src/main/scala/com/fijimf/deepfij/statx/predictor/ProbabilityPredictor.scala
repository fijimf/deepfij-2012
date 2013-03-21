package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.Game

trait ProbabilityPredictor extends WinnerPredictor {
  def winProbability(g: Game): Option[(Double, Double)]

  def winner(g: Game) = winProbability(g) match {
    case Some((h, a)) if h > a => Some(g.homeTeam)
    case Some((h, a)) if h < a => Some(g.homeTeam)
    case _ => None
  }
}
