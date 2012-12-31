package com.fijimf.deepfij.view.mappers

import org.apache.shiro.subject.Subject
import com.fijimf.deepfij.modelx.{Game, Conference, Team}
import java.text.SimpleDateFormat
import java.util.Date

import scala.math._

trait TemplateMapper[K] {
  def apply(k: K): Map[String, Any]


}

object SubjectMapper extends TemplateMapper[Subject] {
  def apply(subject: Subject) = {
    if (subject.isRemembered || subject.isAuthenticated) {
      Map("user" -> Map("name" -> subject.getPrincipal.toString))
    } else {
      Map.empty[String, Any]
    }
  }
}

object TeamMapper extends TemplateMapper[Team] {
  def apply(team: Team) = {
    Map(
      "title" -> team.name,
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
  }

  val fmt = new SimpleDateFormat("MM-dd-yyyy")

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
      "score" -> g.resultOpt.map(rslt => "%d - %d".format(rslt.homeScore, rslt.awayScore)).getOrElse(""),
      "oppSummary" -> (if (g.resultOpt.isDefined) oppSummary(opp, g.date) else "")
    )
  }

  def oppSummary(t: Team, d: Date): String = {
    val fmt = new SimpleDateFormat("MMM d")
    val last = t.games.filter(_.date.before(d)).headOption match {
      case None => ""
      case Some(g) => {
        if (g.isWin(t)) {
          "Defeated %s %d-%d %s.".format(g.loser.get.name, max(g.result.homeScore, g.result.awayScore), min(g.result.homeScore, g.result.awayScore), fmt.format(g.date))
        } else {
          "Lost to %s %d-%d %s.".format(g.winner.get.name, max(g.result.homeScore, g.result.awayScore), min(g.result.homeScore, g.result.awayScore), fmt.format(g.date))
        }
      }
    }
    "(%d - %d, %d - %d %s) %s".format(
      t.wins.filter(_.date.before(d)).size, t.losses.filter(_.date.before(d)).size,
      t.conferenceWins.filter(_.date.before(d)).size, t.conferenceLosses.filter(_.date.before(d)).size,
      t.conference.name.replaceAll( """Conference$|League$|Association$""", "").trim, last)

  }
}

object ConferenceMapper extends TemplateMapper[Conference] {
  def apply(conf: Conference) = {
    Map("title" -> conf.name, "name" -> conf.name,
      "key" -> conf.key,
      "standings" -> conf.standings
    )
  }
}
