package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.ScheduleDao
import java.text.SimpleDateFormat
import java.util.Date

object AdminPanel {
  val sd = new ScheduleDao()
  val dateFmt = new SimpleDateFormat("MMM-dd-yyyy")
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

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
                <th>Key</th> <th>Name</th> <th>Teams</th> <th>Games</th> <th>First</th> <th>Last</th> <th colspan="3"></th>
              </tr>
            </thead>
            <tbody>
              {sd.findAll().map(s => {
              val lastDate: Date = s.gameList.maxBy(_.date).date
              val firstDate: Date = s.gameList.minBy(_.date).date
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
                  {dateFmt.format(firstDate)}
                </td>
                <td>
                  {dateFmt.format(lastDate)}
                </td>
                <td>
                  <a class="btn btn-success" href={"/admin/update?key=" + s.key + "&name=" + s.name}>Update</a>
                </td>
                <td>
                  <a class="btn btn-primary" href={"/admin/rebuild?key=" + s.key + "&name=" + s.name + "&from=" + firstDate + "&to=" + lastDate}>Rebuild</a>
                </td>
                <td>
                  <a class="btn btn-danger" href={"/admin/delete?key=" + s.key + "&name=" + s.name}>Delete</a>
                </td>
              </tr>
            })}
            </tbody>
          </table>
        </div>
      </div>
      <div class="row">
        <div class="span6">
          <form class="well" method="GET" action="/admin/new">
            <label>Name</label>
              <input id="name" name="name" type="text" class="span3" placeholder="Schedule name..."/>
            <label>Key</label>
              <input id="key" name="key" type="text" class="span3" placeholder="schedule-key"/>
            <label>From</label>
              <input id="from" name="from" type="text" class="span3" placeholder="yyyymmdd"/>
            <label>Key</label>
              <input id="to" name="to" type="text" class="span3" placeholder="yyyymmdd"/>
            <button type="submit" class="btn btn-primary">New Schedule</button>
          </form>
        </div>
      </div>
  }
}