package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.modelx.Team
import com.fijimf.deepfij.statx.{StatInfoImpl, TeamModel, StatisticalModel}

class LeastSquaresModel  extends StatisticalModel[Team] with TeamModel {
  def statistics = List.empty[StatInfoImpl]
}
