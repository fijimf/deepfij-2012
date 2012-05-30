package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Team}


trait TeamStatistic extends Statistic[Team] {
  override def keys(s: Schedule) = s.teamList
}



