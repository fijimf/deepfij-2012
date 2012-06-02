package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Team}


trait TeamStatistic extends Statistic[Team] {
  override def keys(s: Schedule) = s.teamList

  def startDate(s: Schedule) = s.gameList.minBy(_.date).date

  def endDate(s: Schedule) = s.gameList.maxBy(_.date).date
}



