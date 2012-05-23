package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.ScheduleDao
import java.text.SimpleDateFormat
import java.util.Date

object ScheduleListPanel {
  val sd = new ScheduleDao()
  val dateFmt = new SimpleDateFormat("MMM-dd-yyyy")
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def apply() = {
      <div class="row">
        <div class="span10">
          <h3>Schedules</h3>
          <table class="table table-bordered table-condensed">
            <thead>
              <tr>
                <th>Key</th> <th>Name</th> <th>Teams</th> <th>Games</th> <th>First</th> <th>Last</th>
              </tr>
            </thead>
            <tbody>
              {sd.findAll().map(s => {
              val lastDate:Option[Date] = s.gameList match {
                case Nil=> None
                case gameList=> Some(gameList.maxBy(_.date).date)
              }
              val firstDate: Option[Date]  = s.gameList match {
                case Nil=> None
                case gameList=> Some(gameList.minBy(_.date).date)
              }
              <tr>
                <td>
                  {s.key}
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
\              </tr>
            })}
            </tbody>
          </table>
        </div>
      </div>
  }
}