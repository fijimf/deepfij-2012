package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.statx.{TeamModel, StatisticalModel}
import com.fijimf.deepfij.modelx.{MetaStat, Team}


class FixedPointModel extends StatisticalModel[Team] with TeamModel {

  def name = null

  def key = null

  def statistics = List.empty[MetaStat]
}
