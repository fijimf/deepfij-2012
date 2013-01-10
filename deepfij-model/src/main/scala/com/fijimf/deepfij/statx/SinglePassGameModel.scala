package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Game}
import java.util.Date

trait SinglePassGameModel[T] extends StatisticalModel[T] {

  def processGames(d: Date, gs: List[Game], ctx: ModelContext[T]): ModelContext[T]

  override def process(s: Schedule, context: ModelContext[T] = ModelContext[T](), from: Option[Date] = None, to: Option[Date] = None) = {
    val gamesByDate: List[(Date, List[Game])] = s.gameList.groupBy(_.date).toList.sortBy(_._1)
    gamesByDate.foldLeft(context) {
      case (ctx, (date, games)) => processGames(date, games, ctx)
    }
  }
}