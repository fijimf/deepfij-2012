package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx.{Game, Schedule, MetaStat, Team}
import org.apache.mahout.math.{RandomAccessSparseVector, DenseVector, SparseMatrix}
import java.util.Date
import org.apache.mahout.math.solver.LSMR
import com.fijimf.deepfij.util.DateStream
import org.apache.log4j.Logger
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.special.Erf

class LinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "linear-regression"

  val statW: MetaStat = new MetaStat(statKey = "win-predictor", name = "Win Predictor", format = "%9.5f", higherIsBetter = true)
  val statP: MetaStat = new MetaStat(statKey = "point-predictor", name = "Point Predictor", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statW, statP)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))



    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap
    val a: java.util.Map[java.lang.Integer, RandomAccessSparseVector] = new java.util.HashMap[java.lang.Integer, RandomAccessSparseVector]()
    games.zipWithIndex.foreach(pair => {
      val r = new RandomAccessSparseVector(teamMap.size)
      r.set(teamMap(pair._1.homeTeam.key), 1.0)
      r.set(teamMap(pair._1.awayTeam.key), -1.0)
      a.put(pair._2, r)
    })
    val A = new SparseMatrix(games.size, teamMap.size, a)
    val b0 = new DenseVector(games.map(g => math.signum(margin(g))).toArray)
    val b1 = new DenseVector(games.map(g => margin(g)).toArray)

    val r1: (ModelContext[Team]) => ModelContext[Team] = runRegression(A, b0, teamMap, _, date, s, statW)
    val r2: (ModelContext[Team]) => ModelContext[Team] = runRegression(A, b1, teamMap, _, date, s, statP)

    r1.andThen(r2).apply(ctx)

  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble

  def runRegression(A: SparseMatrix, b: DenseVector, teamMap: Map[String, Int], ctx: ModelContext[Team], date: Date, s: Schedule, statInfo: MetaStat): ModelContext[Team] = {
    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val x = lsmr.solve(A, b)
    teamMap.foldLeft(ctx)((ctx, pair) => ctx.update(statInfo, date, s.teamByKey(pair._1), x.get(pair._2)))
  }
}

class HomeAdjustedLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "homadj-linear-regression"

  val statHAPP: MetaStat = new MetaStat(statKey = "homadj-point-predictor", name = "Home Adj Point Predictor", format = "%9.5f", higherIsBetter = true)



  def statistics = List(statHAPP)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap
    val a: java.util.Map[java.lang.Integer, RandomAccessSparseVector] = new java.util.HashMap[java.lang.Integer, RandomAccessSparseVector]()
    games.zipWithIndex.foreach(pair => {
      val r = new RandomAccessSparseVector(teamMap.size+1)
      r.set(teamMap.size, 1.0)
      r.set(teamMap(pair._1.homeTeam.key), 1.0)
      r.set(teamMap(pair._1.awayTeam.key), -1.0)
      a.put(pair._2, r)
    })
    val A = new SparseMatrix(games.size,1+ teamMap.size, a)

    val b = new DenseVector(games.map(g => margin(g)).toArray)

    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val x = lsmr.solve(A, b)
    println(x.get(teamMap.size))
    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statHAPP, date, s.teamByKey(pair._1), x.get(pair._2))
    })
  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble

}
class HomeAwayLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "ha-linear-regression"

  val statHome: MetaStat = new MetaStat(statKey = "home-point-predictor", name = "Home Point Predictor", format = "%9.5f", higherIsBetter = true)
  val statAway: MetaStat = new MetaStat(statKey = "away-point-predictor", name = "Away Point Predictor", format = "%9.5f", higherIsBetter = true)
  val statHADiff: MetaStat = new MetaStat(statKey = "home-away-point-diff", name = "H/A Point Predictor Diff", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statHome, statAway, statHADiff)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap
    val a: java.util.Map[java.lang.Integer, RandomAccessSparseVector] = new java.util.HashMap[java.lang.Integer, RandomAccessSparseVector]()
    games.zipWithIndex.foreach(pair => {
      val r = new RandomAccessSparseVector(2 * teamMap.size)
      r.set(teamMap(pair._1.homeTeam.key), 1.0)
      r.set(teamMap(pair._1.awayTeam.key) + teamMap.size, -1.0)
      a.put(pair._2, r)
    })
    val A = new SparseMatrix(games.size,2* teamMap.size, a)

    val b = new DenseVector(games.map(g => margin(g)).toArray)

    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val x = lsmr.solve(A, b)
    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statHome, date, s.teamByKey(pair._1), x.get(pair._2)).
        update(statAway, date, s.teamByKey(pair._1), x.get(pair._2+teamMap.size)).
        update(statHADiff, date, s.teamByKey(pair._1), x.get(pair._2 - pair._2+teamMap.size))
    })
  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble

}
