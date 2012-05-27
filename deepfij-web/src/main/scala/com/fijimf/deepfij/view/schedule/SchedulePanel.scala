package com.fijimf.deepfij.view.schedule

import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.Team
import java.util.Date


object SchedulePanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")

  def logoImage(team: Team) = {
    <a href={team.officialUrlOpt.getOrElse("#")} target="_blank">
        <img src={team.logoOpt.get}/>
    </a>
  }

  def apply(date: Date) = {
    <div class="row">
      <div class="span12">
        <h3>
          {"Search results for ''"}
        </h3>
      </div>
    </div>
  }
}