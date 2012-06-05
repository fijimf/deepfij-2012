package com.fijimf.deepfij.view

import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.Team


object SearchResultPanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")

  def logoImage(team: Team) = {
    <a href={team.officialUrlOpt.getOrElse("#")} target="_blank">
        <img src={team.logoOpt.get}/>
    </a>
  }

  def apply(q: String, teams: List[Team]) = {
    <div class="row">
      <div class="span12">
        <h3>
          {"Search results for '" + q + "'"}
        </h3>
      </div>
    </div>


      <div class="row">
        <div class="span12">
          <table class="table table-bordered table-condensed">
            <thead>
              <tr>
                <th>Team</th>
                <th>Conference</th>
                <th>Overall</th>
                <th>Conference</th>
              </tr>
            </thead>
            <tbody>
              {teams.map(t => {
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
                  {t.conference.name}
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