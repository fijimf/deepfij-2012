package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{ModelContext, TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx.{Game, Schedule, MetaStat, Team}
import org.apache.mahout.math.{RandomAccessSparseVector, DenseVector, SparseMatrix}
import java.util.Date
import org.apache.mahout.math.solver.LSMR
import com.fijimf.deepfij.util.DateStream
import org.apache.log4j.Logger

class LeastSquaresModel extends StatisticalModel[Team] with TeamModel {
  val log = Logger.getLogger(this.getClass)
  val statX: MetaStat = new MetaStat(statKey = "x", name = "x", format = "%9.5f", higherIsBetter = true)

  def statistics = List(statX)

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
    val b = new DenseVector(games.map(g => (g.result.homeScore - g.result.awayScore).toDouble).toArray)
    val lsmr: LSMR = new LSMR()
    lsmr.setIterationLimit(10000)
    lsmr.setAtolerance(0.00001)
    lsmr.setBtolerance(0.00001)
    val x = lsmr.solve(A, b)
    teamMap.foldLeft(ctx)((ctx, pair) => ctx.update(statX, date, s.teamByKey(pair._1), x.get(pair._2)))


  }
}
