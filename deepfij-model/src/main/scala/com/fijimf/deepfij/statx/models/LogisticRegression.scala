package com.fijimf.deepfij.statx.models

import org.apache.mahout.classifier.sgd.{L1, PriorFunction, OnlineLogisticRegression}
import java.io.{DataOutput, DataInput}
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx.{Game, Team, Schedule, MetaStat}
import com.fijimf.deepfij.statx.{TeamModel, StatisticalModel, ModelContext}
import java.util.Date
import com.fijimf.deepfij.util.DateStream
import org.apache.mahout.math.{DenseVector, SparseMatrix, RandomAccessSparseVector}
import org.apache.mahout.math.solver.LSMR

class LogisticRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Logistic Regression Model"

  def key = "logistic-regression"

  val statW: MetaStat = new MetaStat(statKey = "log-win-predictor", name = "Log Win Predictor", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statW)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap

    val regr: OnlineLogisticRegression = new OnlineLogisticRegression(2, teamMap.size, new L1)
    games.foreach(g => {
      val r = new RandomAccessSparseVector(teamMap.size)
      r.set(teamMap(g.homeTeam.key), 1.0)
      r.set(teamMap(g.awayTeam.key), -1.0)

      if (g.result.homeScore > g.result.awayScore) {
        regr.train(1, r)
      }
      else {
        regr.train(0, r)
      }
    })
    regr.close()
    val beta = regr.getBeta.viewRow(0)

    teamMap.foldLeft(ctx)((ctx, pair) => ctx.update(statW, date, s.teamByKey(pair._1), beta.get(pair._2)))
  }

}
