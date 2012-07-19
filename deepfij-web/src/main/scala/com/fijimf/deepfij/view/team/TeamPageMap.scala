package com.fijimf.deepfij.view.team

import com.fijimf.deepfij.modelx.Team
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject


class TeamPageMap {
  def apply(team: Team): Map[String, Any] = {
    val ctx = Map(
      "name" -> team.name,
      "key" -> team.key,
      "conference" -> Map("name" -> team.conference.name, "key" -> team.conference.key),
      "wins" -> team.wins.size,
      "losses" -> team.losses.size,
      "conferenceWins" -> team.wins.filter(g => g.homeTeam.conference == g.awayTeam.conference).size,
      "conferenceLosses" -> team.losses.filter(g => g.homeTeam.conference == g.awayTeam.conference).size
    "games" -> team.games.map(g => {
      val opp = if (g.homeTeam == team) g.awayTeam else g.homeTeam
      val result = if (g.resultOpt.isDefined) {
        Map(
          "wl" -> if (g.isWin(team)) "W" else "L",
        "score" -> "%d - %d".format(g.resultOpt.get.homeScore, g.resultOpt.get.awayScore)
        )
      }
      Map(
        "opponent" -> Map("name" -> opp.name, "key" -> opp.key)
      "date" -> g.date.toString

      )


    })
    )
    val subject: Subject = SecurityUtils.getSubject
    val userCtx = if (subject.isRemembered || subject.isAuthenticated) {
      Map("user" -> subject.getPrincipal)
    } else {
      Map.empty[String, Any]
    }
    ctx ++ userCtx
  }

}
