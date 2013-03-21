package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.{Schedule, Team, Game}
import java.util.Date

trait WinnerPredictor {
  def winner(g: Game): Option[Team]

  def cumulativeAccuracy(s: Schedule): Map[Date, Double] = {
    val gamesByDate: List[(Date, List[Game])] = s.gameList.groupBy(_.date).toList.sortBy(_._1)
    val results: List[(Date, (Int, Int))] = gamesByDate.foldLeft(List.empty[(Date, (Int, Int))])((lst: List[(Date, (Int, Int))], pair: (Date, List[Game])) => {
      val (date, games) = pair

      val (correct, picked) = accuracy(games)
      accumulateDailyAccuracy(lst, date, correct, picked)
    })
    results.filter(_._2._2 != 0).toMap.mapValues(p => p._1.toDouble / p._2.toDouble)
  }


  def accumulateDailyAccuracy(lst: List[(Date, (Int, Int))], date: Date, fn: Int, fd: Int): List[(Date, (Int, Int))] = {
    lst.headOption match {
      case Some((_, (num, den))) => (date, (num + fn, den + fd)) :: lst
      case None => (date, (fn, fd)) :: lst
    }
  }

  def accuracy(games: List[Game]): (Int, Int) = {
    games.foldLeft((0, 0))((fract: (Int, Int), game: Game) => {
      (game.winner, winner(game)) match {
        case (Some(g), Some(h)) if g == h => (fract._1 + 1, fract._2 + 1)
        case (Some(g), Some(h)) => (fract._1, fract._2 + 1)
        case _ => fract
      }
    })
  }
}
