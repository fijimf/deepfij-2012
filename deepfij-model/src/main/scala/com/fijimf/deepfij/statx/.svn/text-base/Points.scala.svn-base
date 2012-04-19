package com.fijimf.deepfij.statx

import com.fijimf.deepfij.modelx.{Schedule, Team}
import java.util.Date


object Points {
  def apply(s: Schedule)(d: Date): Points = {
    val gs = s.gameList.filter(g => g.date.before(d) && g.resultOpt.isDefined)

    new Points() {
    }

  }
}

trait Points {


}