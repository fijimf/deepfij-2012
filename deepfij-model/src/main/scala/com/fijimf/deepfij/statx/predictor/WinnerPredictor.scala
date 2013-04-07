package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.{Schedule, Team, Game}
import java.util.Date

case class Accuracy(correct: Int = 0, missed: Int = 0, skipped: Int = 0) {
  def incCorrect = copy(correct = correct + 1)

  def incMissed = copy(missed = missed + 1)

  def intSkipped = copy(skipped = skipped + 1)

  def +(acc: Accuracy) = Accuracy(correct + acc.correct, missed + acc.missed, skipped + acc.skipped)
}

trait WinnerPredictor {
  def winner(g: Game): Option[Team]

  def gamesByDate(s: Schedule): Map[Date, List[Game]] = s.gameList.groupBy(_.date)

  def dailyAccuracy(s: Schedule): Map[Date, Accuracy] = gamesByDate(s).mapValues(accuracy(_))

  def cumulativeAccuracy(s: Schedule): Map[Date, Accuracy] = {
    val da: Map[Date, Accuracy] = dailyAccuracy(s)
    da.keys.toList.sorted.foldLeft((Accuracy(0, 0, 0), Map.empty[Date, Accuracy]))((pair:(Accuracy, Map[Date, Accuracy]), d: Date) =>
    {
      val newAcc = pair._1 + da(d)
      (newAcc, pair._2 + (d -> newAcc))
    })._2
  }

  def accuracy(games: List[Game]): Accuracy = {
    games.foldLeft(Accuracy(0, 0, 0))((acc: Accuracy, game: Game) => {
      (game.winner, winner(game)) match {
        case (Some(g), Some(h)) if g == h => acc.incCorrect
        case (Some(g), Some(h)) => acc.incMissed
        case _ => acc.intSkipped
      }
    })
  }
}
