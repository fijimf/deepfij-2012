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
                  <form method="POST" action="/admin/update">
                    <input id="name" name="name" type="hidden" value={s.name}/>
                    <input id="key" name="key" type="hidden" value={s.key}/>
                    <input id="from" name="from" type="hidden" value={yyyymmdd.format(firstDate)}/>
                    <input id="to" name="to" type="hidden" value={yyyymmdd.format(new Date)}/>
                    <button type="submit" class="btn btn-success">Update</button>
                  </form>
                </td>
                <td>
                  <form method="POST" action="/admin/rebuild">
                    <input id="name" name="name" type="hidden" value={s.name}/>
                    <input id="key" name="key" type="hidden" value={s.key}/>
                    <input id="from" name="from" type="hidden" value={yyyymmdd.format(firstDate)}/>
                    <input id="to" name="to" type="hidden" value={yyyymmdd.format(new Date)}/>
                    <button type="submit" class="btn btn-primary">Rebuild</button>
                  </form>
                </td>
                <td>
                  <form method="POST" action="/admin/delete">
                    <input id="key" name="key" type="hidden" value={s.key}/>
                    <button type="submit" class="btn btn-danger">Delete</button>
                  </form>
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