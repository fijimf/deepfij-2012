package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.Schedule

trait Builder[T] {
  def build(schedule: Schedule, data: Map[String, String]): Option[T]
}
