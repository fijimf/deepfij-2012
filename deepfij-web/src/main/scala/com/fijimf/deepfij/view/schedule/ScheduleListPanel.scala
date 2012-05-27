package com.fijimf.deepfij.view.schedule

import com.fijimf.deepfij.modelx.ScheduleDao
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.log4j.Logger

object ScheduleListPanel {
  val log = Logger.getLogger(this.getClass)
  val sd = new ScheduleDao()
  val dateFmt = new SimpleDateFormat("MMM-dd-yyyy")

  def apply() = {
    <div class="row">
      <div class="span10">
        <h3>Schedules</h3>
        <table class="table table-bordered table-condensed">
          <thead>
            <tr>
              <th></th> <th>Key</th> <th>Name</th> <th>Teams</th> <th>Games</th> <th>First</th> <th>Last</th>
            </tr>
          </thead>
          <tbody>
            {sd.findAll().map(s => {
            log.info(s.key + " " + s.isPrimary)
            val lastDate: Option[Date] = s.gameList match {
              case Nil => None
              case gameList => Some(gameList.maxBy(_.date).date)
            }
            val firstDate: Option[Date] = s.gameList match {
              case Nil => None
              case gameList => Some(gameList.minBy(_.date).date)
            }
            <tr>
              <td>
                {if (s.isPrimary) {
                <i class="icon-star"></i>
              } else {
                <a href={"/schedule/makeprimary/" + s.key}>
                  <i class="icon-star-empty"></i>
                </a>
              }}
              </td>
              <td>
                <a href={"/schedule/edit/" + s.key}>
                  {s.key}
                </a>
              </td>
              <td>
                {s.name}
              </td>
              <td>
                {s.teamList.size}
              </td>
              <td>
                {s.gameList.size}
              </td>
              <td>
                {firstDate.map(dateFmt.format(_)).getOrElse("N/A")}
              </td>
              <td>
                {lastDate.map(dateFmt.format(_)).getOrElse("N/A")}
              </td>
            </tr>
          })}
          </tbody>
        </table>
        <div class="row">
          <div class="span10">
            <a class="btn btn-primary" href="/schedule/new">New Schedule</a>
          </div>
        </div>
      </div>


    </div>
  }
}