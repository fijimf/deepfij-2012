package com.fijimf.deepfij.data

import com.fijimf.deepfij.repo.ScheduleRepository

trait Workflow {
  val repo = new ScheduleRepository

}
