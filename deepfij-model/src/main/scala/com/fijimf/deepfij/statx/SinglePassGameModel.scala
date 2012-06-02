package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Game}

trait SinglePassGameModel[T] extends StatisticalModel[T] {

  def processGame(g: Game, ctx: ModelContext[T]): ModelContext[T]

  override def process(s: Schedule, context: ModelContext[T]) = {
    s.gameList.sortBy(_.date).foldLeft(context) {
      case (ctx, game) => processGame(game, ctx)
    }
  }
}