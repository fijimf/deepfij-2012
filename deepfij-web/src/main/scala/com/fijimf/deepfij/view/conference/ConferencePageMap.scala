package com.fijimf.deepfij.view.conference

import com.fijimf.deepfij.modelx.{Conference, Game, Team}
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import java.text.SimpleDateFormat


object ConferencePageMap {
  val fmt = new SimpleDateFormat("MM-dd-yyyy")

  def apply(team: Conference): Map[String, Any] = {
    val ctx = Map(
      "name" -> conference.name,
      "key" -> conference.key,
      "conferenceLosses" -> team.losses.filter(g => g.homeTeam.conference == g.awayTeam.conference).size,
      "games" -> team.games.sortBy(_.date).map(g => {
        gameToMap(g, team)
      })
    )

    val subject: Subject = SecurityUtils.getSubject
    val userCtx = if (subject.isRemembered || subject.isAuthenticated) {
      Map("user" -> Map("name" -> subject.getPrincipal.toString))
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
