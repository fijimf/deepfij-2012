package com.fijimf.deepfij.view.schedule

import com.fijimf.deepfij.modelx.Schedule
import java.text.SimpleDateFormat
import java.util.Date

object ScheduleShowPanel {
  def apply(s: Schedule)= {
    val dateFmt = new SimpleDateFormat("MMM-dd-yyyy")
    val lastDate: Option[Date] = s.gameList match {
      case Nil => None
      case gameList => Some(gameList.maxBy(_.date).date)
    }
    val firstDate: Option[Date] = s.gameList match {
      case Nil => None
      case gameList => Some(gameList.minBy(_.date).date)
    }
    <div class="row">
      <div class="span6">
        <div class="well">
          <p>Name:
            {s.name}
          </p>
          <p>Key:
            <a href={"/schedule/edit/"+s.key}>{s.key}</a>
          </p>
          <p># of Teams:
            {s.teamList.size}
          </p>
          <p># of Games:
            {s.gameList.size}
          </p>

          <p>Primary:
            {if (s.isPrimary) "Y" else "N"}
          </p>
          <p>Earliest Game:
            {firstDate.map(d => dateFmt.format(d)).getOrElse("N/A")}
          </p>
          <p>Latest Game:
            {lastDate.map(d => dateFmt.format(d)).getOrElse("N/A")}
          </p>
        </div>
      </div>
    </div>
  }

}
