package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.{Schedule, Game}
import java.util.Date

trait ProbabilityPredictor extends WinnerPredictor {
  def winProbability(g: Game): Option[(Double, Double)]

  def logLikelihood(g: Game): Option[Double] = {
    winProbability(g) match {
      case Some((h, a)) => {
        g.resultOpt match {
          case Some(result) => if (result.homeScore > result.awayScore) {
            Some(math.log(h))
          } else {
            Some(math.log(a))
          }
          case None => None
        }
      }
      case None => None
    }
  }

  def winner(g: Game) = winProbability(g) match {
    case Some((h, a)) if h > a => Some(g.homeTeam)
    case Some((h, a)) if h < a => Some(g.awayTeam)
    case _ => None
  }

  def dailyLogLikelihood(s: Schedule): Map[Date, Double] = gamesByDate(s).mapValues(logLikelihood(_))

  def cumulativeLogLikelihood(s: Schedule): Map[Date, Double] = {
    val da: Map[Date, Double] = dailyLogLikelihood(s)
    da.keys.toList.sorted.foldLeft((0.0, Map.empty[Date, Double]))((pair: (Double, Map[Date, Double]), d: Date) => {
      val newLL = pair._1 + da(d)
      (newLL, pair._2 + (d -> newLL))
    })._2
  }

  def logLikelihood(games: List[Game]): Double = games.flatMap(logLikelihood(_)).sum

}
