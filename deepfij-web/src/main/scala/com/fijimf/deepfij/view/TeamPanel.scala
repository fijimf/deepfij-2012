package com.fijimf.deepfij.view

import com.fijimf.deepfij.modelx.{Game, Team}
import java.text.SimpleDateFormat
import xml.NodeSeq


object TeamPanel {
  val fmt = new SimpleDateFormat("M/d/yyyy")

  def logoImage(team: Team) = {
    <a href={team.officialUrlOpt.getOrElse("#")} target="_blank">
        <img src={team.logoOpt.getOrElse("")}/>
    </a>
  }

  def apply(team: Team):NodeSeq = {
    val w = team.wins.size
    val l = team.losses.size
    val cw = team.wins.filter(g => g.homeTeam.conference == g.awayTeam.conference).size
    val cl = team.wins.filter(g => g.homeTeam.conference == g.awayTeam.conference).size
    <div class="row">
      <div class="span1">
        {logoImage(team)}
      </div>
      <div class="span11">
        <h1>
          {team.name + " " + team.nicknameOpt.getOrElse("") + " (" + w + "-" + l + ", " + cw + "-" + cl + ")"}
        </h1>
        <h3>
          <a href={"/conference/"+team.conference.key}>{team.conference.name}</a>
        </h3>
      </div>
    </div>
      <div class="row">
        <div class="span6">
          <table class="table table-bordered table-condensed">
            <tbody>
              {team.games.sortBy(_.date).take(18).map(g => {
              gameRow(g, team)
            })}
            </tbody>
          </table>
        </div>
        <div class="span6">
          <table class="table table-bordered table-condensed">
            <tbody>
              {team.games.sortBy(_.date).drop(18).map(g => {
              gameRow(g, team)
            })}
            </tbody>
          </table>
        </div>
      </div>
  }

  def gameRow(g: Game, t: Team) = {
    val (wl, rowClass) = if (g.isWin(t)) {
      ("W", "row-win")
    } else if (g.isLoss(t)) {
      ("L", "row-loss")
    } else {
      ("", "row-noresult")
    }

    val score = if (g.resultOpt.isDefined) {
      if (g.homeTeam == t) {
        <td>
          {g.result.homeScore + " - " + g.result.awayScore}
        </td>
      } else {
        <td>
          {g.result.awayScore + " - " + g.result.homeScore}
        </td>
      }
    } else {
        <td/>
    }

    //Need to fix for tournament games
    val opp = if (g.homeTeam == t) {
      <td>vs.
        <a href={"/team/" + g.awayTeam.key}>
          {g.awayTeam.name}
        </a>
      </td>
    } else {
      <td>@
        <a href={"/team/" + g.homeTeam.key}>
          {g.homeTeam.name}
        </a>
      </td>
    }
    <tr class={rowClass}>
      <td>
        {fmt.format(g.date)}
      </td> <td>
      {wl}
    </td>{score}{opp}
    </tr>
  }
}