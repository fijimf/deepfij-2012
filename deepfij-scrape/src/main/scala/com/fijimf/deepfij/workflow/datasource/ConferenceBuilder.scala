package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Schedule, Conference}
import com.fijimf.deepfij.util.Util._
import com.fijimf.deepfij.workflow.Builder

trait ConferenceBuilder extends Builder[Conference] {

  def build(schedule: Schedule, data: Map[String, String]): Option[Conference] = {
    for (n <- data.get("name")) yield {
      new Conference(schedule = schedule, name = n, key = nameToKey(n))
    }
  }

}
