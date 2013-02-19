package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx._
import org.apache.mahout.math.{Matrix, RandomAccessSparseVector, DenseVector, SparseMatrix}
import java.util.Date
import org.apache.mahout.math.solver.LSMR
import com.fijimf.deepfij.util.DateStream
import org.apache.log4j.Logger
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.special.Erf
import com.fijimf.deepfij.statx.ModelContext
import com.fijimf.deepfij.util.DateStream

class LinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Simple Linear Regression Model"

  def key = "linear-regression"

  val statW: MetaStat = new MetaStat(modelName = name, modelKey = key, statKey = "win-predictor", name = "Win Predictor", format = "%9.5f", higherIsBetter = true)
  val statP: MetaStat = new MetaStat(modelName = name, modelKey = key, statKey = "point-predictor", name = "Point Predictor", format = "%9.5f", higherIsBetter = true)


  def statistics = List(statW, statP)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))

    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap
    val A = games.zipWithIndex.map(pair => {
      List(
        (pair._2, teamMap(pair._1.homeTeam.key)) -> 1.0,
        (pair._2, teamMap(pair._1.awayTeam.key)) -> -1.0
      )
    }).flatten.toMap

    val b0 = games.map(g => margin(g))
    val b1 = games.map(g => math.signum(margin(g)))

    val x0 = LSMRSolver.solve(A,games.size, teamMap.size,  b0)
    val x1 = LSMRSolver.solve(A,games.size, teamMap.size,  b1)


    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statW, date, s.teamByKey(pair._1), x0(pair._2)).
        update(statP, date, s.teamByKey(pair._1), x1(pair._2))
    })

  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble
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
      val r = new RandomAccessSparseVector(teamMap.size + 1)
      r.set(teamMap.size, 1.0)
      r.set(teamMap(pair._1.homeTeam.key), 1.0)
      r.set(teamMap(pair._1.awayTeam.key), -1.0)
      a.put(pair._2, r)
    })
    val A = new SparseMatrix(games.size, 1 + teamMap.size, a)

    val b = new DenseVector(games.map(g => margin(g)).toArray)

    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val x = lsmr.solve(A, b)
    val homeAdv = x.get(teamMap.size)
    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statHAPP, date, s.teamByKey(pair._1), x.get(pair._2))
    }).updateParm(statHAPP, date, "homeAdv", homeAdv)
  }

  def margin(g: Game): Double = (g.result.homeScore - g.result.awayScore).toDouble

}

class OffenseDefenseLinearRegression extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)

  def name = "Offense-Defense Linear Regression Model"

  def key = "off-def-linear-regression"

  val statOff: MetaStat = new MetaStat(statKey = "off-point-predictor", name = "Home Point Predictor", format = "%9.5f", higherIsBetter = true)
  val statDef: MetaStat = new MetaStat(statKey = "def-point-predictor", name = "Away Point Predictor", format = "%9.5f", higherIsBetter = true)

  def statistics = List(statOff, statDef)

  override def process(s: Schedule, ctx: ModelContext[Team], from: Option[Date], to: Option[Date]) = {
    DateStream(scheduleStartDate(s), scheduleEndDate(s)).foldLeft(ctx)((ctx, d) => processDate(s, d, ctx))
  }

  def processDate(s: Schedule, date: Date, ctx: ModelContext[Team]): ModelContext[Team] = {
    log.info("Processing " + date)
    val games: List[Game] = s.gameList.filter(g => g.resultOpt.isDefined && !g.date.after(date))
    val teamMap: Map[String, Int] = (games.map(_.homeTeam.key) ++ games.map(_.awayTeam.key)).toSet.toList.sorted.zipWithIndex.toMap



    val A = games.zipWithIndex.map(pair => {
      val homeScoreRowIndex: Int = pair._2
      val awayScoreRowIndex: Int = games.size + homeScoreRowIndex

      val homeKey: String = pair._1.homeTeam.key
      val awayKey: String = pair._1.awayTeam.key
      val homeOffenseColIndex: Int = teamMap(homeKey)
      val homeDefenseColIndex: Int = homeOffenseColIndex + teamMap.size
      val awayOffenseColIndex: Int = teamMap(awayKey)
      val awayDefenseColIndex: Int = awayOffenseColIndex + teamMap.size
      val scoreBaselineColIndex: Int = 2 * teamMap.size
 //     println("HS Game # %d %s %d %s %d".format(homeScoreRowIndex, homeKey, homeOffenseColIndex,awayKey,awayDefenseColIndex) )
 //     println("AS Game # %d %s %d %s %d".format(awayScoreRowIndex, awayKey, awayOffenseColIndex,homeKey,homeDefenseColIndex)  )
      List(
        (homeScoreRowIndex, homeOffenseColIndex) -> 1.0,
        (homeScoreRowIndex, awayDefenseColIndex) -> 1.0,
        (homeScoreRowIndex, scoreBaselineColIndex) -> 1.0,
        (awayScoreRowIndex, awayOffenseColIndex) -> 1.0,
        (awayScoreRowIndex, homeDefenseColIndex) -> 1.0,
        (awayScoreRowIndex, scoreBaselineColIndex) -> 1.0
      )
    }).flatten.toMap

    val b = games.map(g => g.result.homeScore.toDouble) ++ games.map(g => g.result.awayScore.toDouble)

    val x = LSMRSolver.solve(A,games.size*2, teamMap.size*2+1,  b)
    println("Nans==>"+x.filter(_.isNaN).mkString(">>",",","<<"))
    teamMap.foldLeft(ctx)((ctx, pair) => {
      ctx.update(statOff, date, s.teamByKey(pair._1), x(pair._2)).
        update(statDef, date, s.teamByKey(pair._1), x(pair._2 + teamMap.size))
    })
  }
}

trait LinearRegressionSolver {
  def solve(A: Map[(Int, Int), Double], aRows: Int, aCols: Int, b: List[Double]): List[Double]
}

object LSMRSolver extends LinearRegressionSolver {

  def solve(A: Map[(Int, Int), Double], aRows: Int, aCols: Int, b: List[Double]): List[Double] = {
    val Ai: SparseMatrix = createRASparseMatrix(A, aRows, aCols)


  //  println("Rows %d   Columns %d    ZSum %f  ".format(Ai.numRows(), Ai.numCols(), Ai.zSum()) )
  //  0.until(Ai.numRows()).foreach(r=>println(r+"->"+Ai.viewRow(r).asFormatString()))

    val bi = new DenseVector(b.toArray)

    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(100)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val xi = lsmr.solve(Ai, bi)
    0.until(aCols).map(i=>xi.get(i)).toList

  }

  def createRASparseMatrix(A: Map[(Int, Int), Double], aRows: Int, aCols: Int): SparseMatrix = {
    val a: java.util.Map[java.lang.Integer, RandomAccessSparseVector] = new java.util.HashMap[java.lang.Integer, RandomAccessSparseVector]()
    A.foreach {
      case (p: (Int, Int), d: Double) => {
        if (a.containsKey(p._1)) {
          a.get(p._1).set(p._2, d)
        } else {
          val row = new RandomAccessSparseVector(aCols)
          row.set(p._2, d)
          a.put(p._1, row)
        }
      }
    }

    val Ai = new SparseMatrix(aRows, aCols, a)
    Ai
  }
}
