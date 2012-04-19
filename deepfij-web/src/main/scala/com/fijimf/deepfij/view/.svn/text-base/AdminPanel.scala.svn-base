package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.ScheduleDao
import java.text.SimpleDateFormat

object AdminPanel {
  val sd = new ScheduleDao()
  val dateFmt = new SimpleDateFormat("MMM-dd-yyyy")
  def apply() = {
    <div class="row">
      <div class="span12">
        <h1>
          Deep Fij Admin
        </h1>
      </div>
    </div>
      <div class="row">
        <div class="span12">
          <h3>Schedules</h3>
          <table class="table table-bordered table-condensed">
            <thead>
              <tr>
                <th>Key</th> <th>Name</th> <th>Conferences</th> <th>Teams</th> <th>Games</th> <th>FIrst</th> <th>Last</th>
              </tr>
            </thead>
            <tbody>
              {sd.findAll().map(s => {
              <tr>
                <td>
                  {s.key}
                </td>
                <td>
                  {s.name}
                </td>
                <td>
                  {s.conferenceList.size}
                </td>
                <td>
                  {s.teamList.size}
                </td>
                <td>
                  {s.gameList.size}
                </td>
                <td>
                  {dateFmt.format(s.gameList.minBy(_.date).date)}
                </td>
                <td>
                  {dateFmt.format(s.gameList.maxBy(_.date).date)}
                </td>
                <td>
                  <a href={"/admin?action=rebuild&key="+s.key+"&name="+s.name}>Rebuild</a>
                </td>
              </tr>
            })}
            </tbody>
          </table>
        </div>
      </div>
  }
}