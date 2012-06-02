package com.fijimf.deepfij.statx

import java.util.Date
import com.fijimf.deepfij.modelx.{Game, Team, Schedule}


class PointsModel extends SinglePassGameModel[Team] {
  def keys = null

  def valueKeys(s: Schedule) = null

  def valueStartDate(s: Schedule) = null

  def valueEndDate(s: Schedule) = null

  def processGame(g: Game, ctx: ModelContext[Team]) = null
}