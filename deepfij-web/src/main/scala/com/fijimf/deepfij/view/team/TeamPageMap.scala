package com.fijimf.deepfij.view.team

import com.fijimf.deepfij.modelx.{Game, Team}
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import java.text.SimpleDateFormat


object TeamPageMap {
  val fmt = new SimpleDateFormat("MM-dd-yyyy")

  def apply(team: Team): Map[String, Any] = {
    val ctx = Map(
      "name" -> team.name,
      "key" -> team.key,
      "logoUrl" -> team.logoOpt.getOrElse("#"),
      "officalUrl" -> team.officialUrlOpt.getOrElse("#"),
      "nickname" -> team.nicknameOpt.getOrElse(" "),
      "conference" -> Map("name" -> team.conference.name, "key" -> team.conference.key),
      "wins" -> team.wins.size,
      "losses" -> team.losses.size,
      "conferenceWins" -> team.wins.filter(g => g.homeTeam.conference == g.awayTeam.conference).size,
      "conferenceLosses" -> team.losses.filter(g => g.homeTeam.conference == g.awayTeam.conference).size,
      "games" -> team.games.sortBy(_.date).map(g => {
        gameToMap(g, team)
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


  def gameToMap(g: Game, team: Team): Map[String, Object] = {
    val opp = if (g.homeTeam == team) g.awayTeam else g.homeTeam
    Map(
      "opponent" -> Map("name" -> opp.name, "key" -> opp.key),
      "date" -> fmt.format(g.date),
      "wl" -> (
        if (g.isWin(team))
          "W"
        else if (g.isLoss(team))
          "L"
        else
          ""
        ),
      "score" -> g.resultOpt.map(rslt => "%d - %d".format(rslt.homeScore, rslt.awayScore)).getOrElse("")
    )
  }
}
