package com.fijimf.deepfij.statx.models

import com.fijimf.deepfij.modelx.Team
import com.fijimf.deepfij.statx.{StatInfoImpl, StatisticalModel, TeamModel}


/**Measure
    1) adjPointsFor = pointsFor - Opp(Mean[pointsAgainst])
    2) adjPointsAgainst =  pointsAgainst - Opp(Mean[pointsFor]) (lower is better)
    3) adjMargin = margin + Opp[Mean(margin)]
    4) adjNormPointsFor = adjPointsFor/Opp(StDev[pointsAgainst])
    5) adjNormPointsAgainst = adjPointsAgainst/Opp(StDev[pointsFor])
    6) adjNormMargin = adjMargin/Opp(StDev[margin])
 */
class PointsPlusModel extends StatisticalModel[Team] with TeamModel {
  def statistics = List.empty[StatInfoImpl]
}
