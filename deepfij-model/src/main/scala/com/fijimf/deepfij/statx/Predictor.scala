package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Game, Team}

trait FavoritePredictor {
  def statistic:Statistic[Team]

  def model(g:Game):Option[Team] = {
    val pop: Population[Team] = statistic.population(g.date)
    for (h<- pop.stat(g.homeTeam);
         a<- pop.stat(g.awayTeam)) yield {

    }

  }

}

trait ProbabilityPredictor {

}

trait MarginPredictor {

}

trait ScorePredictor {


}
