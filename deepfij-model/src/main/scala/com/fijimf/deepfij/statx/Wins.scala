package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, Schedule}


case class ModelKey(key: String, higherIsBetter: Boolean) extends MetaStatInfo

case class

trait StatisticalModel[T] {
  type ModelValues = Map[Date, Map[T, Double]]
  type ModelContext = Map[ModelKey, ModelValues]

  def keys: List[ModelKey]

  def init(): ModelContext = {
    keys.map(k => (k -> Map.empty[Date, Map[T, Double]])).toMap
  }

  def process(s: Schedule, ctx: ModelContext): ModelContext = ctx

  def complete(ctx: ModelContext): ModelContext = ctx

  def valueKeys(s: Schedule): List[T]

  def valueStartDate(s: Schedule): Date

  def valueEndDate(s: Schedule): Date

  def createStatistics(s: Schedule): Map[String, Statistic[T]] = {
    val ctx: ModelContext = complete(process(s, init()))
    for (k <- keys;
         m <- ctx.get(k)) yield (k -> new Statistic[T] {
      def keys(s: Schedule) = valueKeys(s)

      def startDate(sp: Schedule) = {
        require(sp == s)
        valueStartDate(s)
      }

      def endDate(sp: Schedule) = {
        require(sp == s)
        valueEndDate(s)
      }

      def function(sp: Schedule, k: T, d: Date) = {
        requires (sp == s)
        for (p <- m.get(d); q <- p.get(k)) yield q
      }

      def name = k.name

      def higherIsBetter = k.higherIsBetter
    }).toMap
  }
}

trait SinglePassGameModel[T] extends StatisticalModel[T] {

  def processGame(g: Game, ctx: ModelContext): ModelContext

  override def process(s: Schedule, context: ModelContext) = {
    s.gameList.sortBy(_.date).foldLeft(context) {
      case (ctx, game) => processGame(game, ctx)
    }
  }
}

class WonLostModel extends SinglePassGameModel[Team] {
  val w: ModelKey = ModelKey("wins", true)
  val l: ModelKey = ModelKey("losses", false)
  val wp: ModelKey = ModelKey("wp", true)
  val ws: ModelKey = ModelKey("win-streak", true)
  val ls: ModelKey = ModelKey("loss-streak", false)

  def keys = {
    List(w, l, wp, ws, ls)
  }

  def valueKeys(s: Schedule) = s.teamList.sortBy(_.name)

  def valueStartDate(s: Schedule) = s.gameList.minBy(_.date).map(_.date)

  def valueEndDate(s: Schedule) = s.gameList.maxBy(_.date).map(_.date)

  def processGame(g: Game, ctx: ModelContext) = {
    val wins: ModelValues = ctx(w)
    ctx+=wins.get(g.date) match {
      case Some(m)=>{

      }
      case None=> {
        val m=Map(g.winner->1)
      }
    }
  }
}
