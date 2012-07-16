package com.fijimf.deepfij.view.team

import com.fijimf.deepfij.modelx.{Game, Team}
import xml.NodeSeq
import java.text.SimpleDateFormat

object TeamEditPanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")

  def apply(team: Option[Team]): NodeSeq = {
    <div class="row">
      <div class="span1">

      </div>
      <div class="span11">

        <h3>

        </h3>
      </div>
    </div>
  }

}
