package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Game, Team}
import java.util.Date
import org.apache.commons.lang.time.DateUtils


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

trait SingleStatisticWinnerPredictor extends WinnerPredictor {
  def statistic: Statistic[Team]

  def winner(g: Game): Option[Team] = {
    val d: Date = DateUtils.addDays(g.date, -1)
    val pop: Population[Team] = statistic.population(d)
    (pop.stat(g.homeTeam), pop.stat(g.awayTeam)) match {
      case (Some(h), Some(a)) => {
        if (statistic.higherIsBetter) {
          if (h > a) Some(g.homeTeam) else if (a > h) Some(g.awayTeam) else None
        } else {
          if (h > a) Some(g.awayTeam) else if (a > h) Some(g.homeTeam) else None
        }
      }
      case _ => None
    }
  }
}

trait ProbabilityPredictor extends WinnerPredictor {
  def winProbability(g: Game): Option[(Double, Double)]

  def winner(g: Game) = winProbability(g) match {
    case Some((h, a)) if h > a => Some(g.homeTeam)
    case Some((h, a)) if h < a => Some(g.homeTeam)
    case _ => None
  }
}

trait MarginPredictor {

}

trait ScorePredictor {


}
