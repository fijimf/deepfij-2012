package com.fijimf.deepfij.statx.predictor

import com.fijimf.deepfij.modelx.{Schedule, Game, Team}
import java.util.Date
import org.apache.commons.lang.time.DateUtils
import com.fijimf.deepfij.statx.{Population, Statistic}
import org.apache.mahout.classifier.sgd.{L1, OnlineLogisticRegression}
import org.apache.mahout.math.DenseVector


trait NaiveSingleStatisticPredictor extends ProbabilityPredictor {
  def statistic: Statistic[Team]

  def winProbability(g: Game) = {
    val d: Date = DateUtils.addDays(g.date, -1)
    val pop: Population[Team] = statistic.population(d)
    (pop.zScore(g.homeTeam), pop.zScore(g.awayTeam)) match {
      case (Some(h), Some(a)) => {
        val p = if (statistic.higherIsBetter) {
          1.0 / (1.0 + scala.math.exp(h - a))
        } else {
          1.0 / (1.0 + scala.math.exp(a - h))
        }
        Some((p, 1.0 - p))
      }
      case _ => None
    }
  }
}

class SingleStatisticFeatureMapper(t: Statistic[Team]) extends FeatureMapper {
  def dim = 1

  def f(g: Game) = {
    val d: Date = DateUtils.addDays(g.date, -1)
    val pop: Population[Team] = t.population(d)

    (pop.zScore(g.homeTeam), pop.zScore(g.awayTeam)) match {
      case (Some(h), Some(a)) => Some(Array(h - a))
      case _ => None
    }
  }
}

class SingleStatisticPolyFeatureMapper(t: Statistic[Team]) extends FeatureMapper {
  def dim = 3

  def f(g: Game) = {
    val d: Date = DateUtils.addDays(g.date, -1)
    val pop: Population[Team] = t.population(d)

    (pop.zScore(g.homeTeam), pop.zScore(g.awayTeam)) match {
      case (Some(h), Some(a)) => Some(Array( h - a,pop.fractionalRank(g.homeTeam).get,pop.fractionalRank(g.awayTeam).get) )
      case _ => None
    }
  }
}

class GenericLogisticRegression(s: Schedule, fm: FeatureMapper) extends ProbabilityPredictor {

  val logreg: OnlineLogisticRegression = new OnlineLogisticRegression(2, fm.dim, new L1())
  logreg.lambda(0.5)
  s.gameList.filter(_.resultOpt.isDefined).foreach(g => {
    fm.f(g) match {
      case Some(featureVec) => {
        logreg.train(homeWinner(g), new DenseVector(featureVec))
      }
      case None =>
    }
  })

  println(logreg.getBeta.viewRow(0))

  def winProbability(g: Game) = {
    fm.f(g) match {
      case Some(featureVec) => {
        val p = logreg.classifyScalar(new DenseVector(featureVec))
        Some((1 - p, p))
      }
      case _ => None
    }
  }

  def homeWinner(g: Game): Int = if (g.isWin(g.homeTeam)) 1 else 0

}

trait FeatureMapper {
  def dim: Int

  def f(g: Game): Option[Array[Double]]
}

