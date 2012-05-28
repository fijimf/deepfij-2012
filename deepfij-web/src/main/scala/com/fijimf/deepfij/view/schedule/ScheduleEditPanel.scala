package com.fijimf.deepfij.view.schedule

import com.fijimf.deepfij.modelx.Schedule
import xml.{Node, NodeSeq, Elem}
import java.text.SimpleDateFormat
import java.util.Date

object ScheduleEditPanel {
  val dateFmt = new SimpleDateFormat("yyyyMMdd")

  def apply(s: Schedule): NodeSeq = {
    Seq[Node](
    <h1>Edit Schedule</h1>,
      renameDiv(s),
      rebuildDiv(s),
      updateResultsDiv(s),
      recalcStatsDiv(s),
      deleteDiv(s)
    )
  }

  def deleteDiv(s: Schedule): Elem = {
    <div class="row">
      <div class="span4">
        <form class="well form-inline" method="POST" action="/schedule/makeprimary">
            <label>Set '{s.key}' as primary.</label><input id="key" name="key" type="hidden" value={s.key}/>
          <button type="submit" class="btn btn-success">Primary</button>
        </form>
      </div>
      <div class="span4">
        <form class="well form-inline" method="POST" action="/schedule/delete">
          <label>Delete '{s.key}'.</label><input id="key" name="key" type="hidden" value={s.key}/>
          <button type="submit" class="btn btn-danger">Delete</button>
        </form>
      </div>
    </div>
  }

  def recalcStatsDiv(s: Schedule): Elem = {
    val from = dateFmt.format(s.gameList match {
      case Nil => new Date
      case gameList => gameList.minBy(_.date).date
    })
    val to = dateFmt.format(new Date)

    <div class="row">
      <div class="span8">
        <form class="well form-inline" method="POST" action="/schedule/recalc">
          <label>From</label>
            <input id="from" name="from" type="text" class="span2" value={from}/>
          <label>To</label>
            <input id="to" name="to" type="text" class="span2" value={to}/>
          <button type="submit" class="btn btn-primary">Regenerate Stats</button>
        </form>
      </div>
    </div>
  }

  def updateResultsDiv(s: Schedule): Elem = {
    val from = dateFmt.format(s.gameList match {
      case Nil => new Date
      case gameList => gameList.maxBy(_.date).date
    })
    val to = dateFmt.format(new Date)

    <div class="row">
      <div class="span8">
        <form class="well form-inline" method="POST" action="/schedule/results">
          <label>From</label>
            <input id="from" name="from" type="text" class="span2" value={from}/>
          <label>To</label>
            <input id="to" name="to" type="text" class="span2" value={to}/>
          <button type="submit" class="btn btn-primary">Update Results</button>
        </form>
      </div>
    </div>
  }

  def rebuildDiv(s: Schedule): Elem = {
    val from = dateFmt.format(s.gameList match {
      case Nil => new Date
      case gameList => gameList.minBy(_.date).date
    })
    val to = dateFmt.format(new Date)

    <div class="row">
      <div class="span8">
        <form class="well form-inline" method="POST" action="/schedule/rebuild">
          <label>From</label>
            <input id="from" name="from" type="text" class="span2" value={from}/>
          <label>To</label>
            <input id="to" name="to" type="text" class="span2" value={to}/>
          <button type="submit" class="btn btn-primary">Rebuild</button>
        </form>
      </div>
    </div>
  }

  def renameDiv(s: Schedule): Elem = {
    <div class="row">
      <div class="span8">
        <form class="well form-inline" method="POST" action="/schedule/rename">
          <label>Name</label>
            <input id="name" name="name" type="text" class="span3" value={s.name}/>

            <input id="key" name="key" type="hidden" class="hidden" value={s.key}/>
          <button type="submit" class="btn btn-primary">Rename</button>
        </form>
      </div>
    </div>
  }
}
