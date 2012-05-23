package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.Schedule

object ScheduleEditPanel {
  def apply(s: Schedule) {
    <div class="row">
      <div class="span6">
        <form class="well" method="POST" action="/schedule/update">
          <label>Name</label>
            <input id="name" name="name" type="text" class="span3" placeholder="Schedule name..."/>
          <label>Key</label>
            <input id="key" name="key" type="text" class="span3" placeholder="schedule-key"/>
          <label>Primary?</label>
            <input id="isPrimary" name="isPrimary" type="checkbox" class="span3" value={s.isPrimary.toString}/>
          <button type="submit" class="btn btn-primary">Update</button>
        </form>
      </div>
    </div>
      <div class="row">
        <div class="span6">
          <form class="well" method="POST" action="/schedule/rebuild">
            <label>From</label>
              <input id="from" name="from" type="text" class="span3" placeholder="yyyymmdd"/>
            <label>To</label>
              <input id="to" name="to" type="text" class="span3" placeholder="yyyymmdd"/>
            <button type="submit" class="btn btn-primary">Rebuild</button>
          </form>
        </div>
      </div>

      <div class="row">
        <div class="span6">
          <form class="well" method="POST" action="/schedule/rebuild">
            <label>From</label>
              <input id="from" name="from" type="text" class="span3" placeholder="yyyymmdd"/>
            <label>To</label>
              <input id="to" name="to" type="text" class="span3" placeholder="yyyymmdd"/>
            <button type="submit" class="btn btn-primary">UpdateGames</button>
          </form>
        </div>
      </div>
      <div class="row">
        <div class="span6">
          <form class="well" method="POST" action="/schedule/recalc">
            <label>From</label>
              <input id="from" name="from" type="text" class="span3" placeholder="yyyymmdd"/>
            <label>To</label>
              <input id="to" name="to" type="text" class="span3" placeholder="yyyymmdd"/>
            <button type="submit" class="btn btn-primary">Regenerate Stats</button>
          </form>
        </div>
      </div>
      <div class="row">
        <form method="POST" action="/schedule/delete">
            <input id="key" name="key" type="hidden" value={s.key}/>
          <button type="submit" class="btn btn-danger">Delete</button>
        </form>
      </div>

  }

}
