package com.fijimf.deepfij.view

import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.Conference
import xml.NodeSeq


object ConferencePanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")

  def apply(conference: Conference):NodeSeq = {
    <div class="row">
      <div class="span12">
        <h1>
          {conference.name}
        </h1>
      </div>
    </div>

      <div class="row">
        <div class="span12">
          {conference.standings.take(12).map(t => {
            <img src={t.logoOpt.getOrElse("#")}/>
        })}

        </div>
      </div>

      <div class="row">
        <div class="span8">
          <table class="table table-bordered table-condensed">
            <thead>
              <tr>
                <th>Team</th>
                <th>Overall</th>
                <th>Conference</th>
              </tr>
            </thead>
            <tbody>
              {conference.standings.map(t => {
              val w = t.wins.size
              val l = t.losses.size
              val cw = t.conferenceWins.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
              val cl = t.conferenceLosses.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
              <tr>
                <td>
                  <a href={"/team/" + t.key}>
                    {t.name + " " + t.nicknameOpt.getOrElse("")}
                  </a>
                </td>
                <td>
                  {w + "-" + l}
                </td>
                <td>
                  {cw + "-" + cl}
                </td>
              </tr>
            })}
            </tbody>
          </table>
        </div>
      </div>

  }
}