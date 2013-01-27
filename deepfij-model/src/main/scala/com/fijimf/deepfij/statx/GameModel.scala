package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Team}


trait GameModel {
  self: StatisticalModel[Team] =>

  def scheduleKeys(s: Schedule) = s.teamList.sortBy(_.name)

  def scheduleStartDate(s: Schedule) = s.gameList.minBy(_.date).date

  def scheduleEndDate(s: Schedule) = s.gameList.maxBy(_.date).date

}