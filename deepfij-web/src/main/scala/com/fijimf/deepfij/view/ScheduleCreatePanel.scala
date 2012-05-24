package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.Schedule

object ScheduleCreatePanel {
  def apply()= {
    <div class="row">
      <div class="span6">
        <form class="well" method="POST" action="/schedule/new">
          <label>Name</label>
            <input id="name" name="name" type="text" class="span3" placeholder="Schedule name..."/>
          <label>Key</label>
            <input id="key" name="key" type="text" class="span3" placeholder="schedule-key"/>
          <label>From</label>
            <input id="from" name="from" type="text" class="span3" placeholder="yyyymmdd"/>
          <label>To</label>
            <input id="to" name="to" type="text" class="span3" placeholder="yyyymmdd"/>
          <button type="submit" class="btn btn-primary">Create</button>
        </form>
      </div>
    </div>
  }
}
