package com.fijimf.deepfij.view.mappers

import org.apache.shiro.subject.Subject
import com.fijimf.deepfij.modelx.{Schedule, Game, Conference, Team}
import java.text.SimpleDateFormat
import java.util.Date

import scala.math._
import org.apache.commons.lang.time.DateUtils
import com.fijimf.deepfij.statx.Population
import com.fijimf.deepfij.server.Util

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

object TeamMapper {
  def apply(team: Team, stats: Map[String, Population[Team]]) = {
    val statValues: Map[String, Map[String, Any]] = (for (k <- List("point-predictor", "win-predictor", "points-for-mean", "points-against-mean", "score-margin-mean");
                                                          stat <- stats.get(k);
                                                          rank <- stat.rank(team);
                                                          value <- stat.stat(team)) yield {
      k -> Map("name" -> stat.name, "rank" -> Util.ordinal(rank.toInt), "value" -> stat.format.format(value))
    }).toMap
    statValues

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
      }),
      "stats" -> statValues
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
    val last = t.games.filter(_.date.before(d)).sortBy(_.date).lastOption match {
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
      t.conference.name.replaceAll( """^The """, "").replaceAll( """Conf.$|Conference$|League$|Association$""", "").trim, last)

  }
}

object SearchMapper {
  def apply(schedule: Schedule, q: String) = {
    val qq = q.toLowerCase.trim
    val ts: List[Team] = (schedule.teamList.filter(t => {
      t.key.toLowerCase.contains(qq) ||
        t.name.toLowerCase.contains(qq) ||
        t.longName.toLowerCase.contains(qq) ||
        t.nicknameOpt.map(_.toLowerCase.contains(qq)).getOrElse(false)
    }) ++ schedule.aliasList.filter(_.alias.toLowerCase.contains(qq)).map(_.team)).toSet.toList

    val cs: List[Conference] = schedule.conferenceList.filter(c => {
      c.key.toLowerCase.contains(qq) || c.name.toLowerCase.contains(qq)
    })

    val ds = if (cs.isEmpty && ts.isEmpty) {
      try {
        Option(DateUtils.parseDate(qq, Array("yyyyMMdd", "M/d/yy", "M/d/yyyy", "d-MMM-yy", "d-MMM-yyyy", "MMM d yyyy"))).toList
      }
      catch {
        case ex: Throwable => List.empty[Date]
      }
    } else {
      List.empty[Date]
    }

    Map("title" -> "Search Results", "query" -> q,
      "teams" -> ts,
      "conferences" -> cs,
      "dates" -> ds
    )
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

object DateMapper {
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")
  val fmt = new SimpleDateFormat("MMMM d, yyyy")

  def apply(schedule: Schedule, d: Date, stats: Map[String, Population[Team]]) = {

    val prev = DateUtils.addDays(d, -1)
    val next = DateUtils.addDays(d, 1)
    val (results, games) = schedule.gameList.filter(_.date == d).partition(_.resultOpt.isDefined)
    Map("title" -> d.toString, "date" -> fmt.format(d),
      "prevDate" -> fmt.format(prev), "prevYyyymmdd" -> yyyymmdd.format(prev),
      "nextDate" -> fmt.format(next), "nextYyyymmdd" -> yyyymmdd.format(next),
      "games" -> games.map(g => gameToMap(g, stats)), "hasGames" -> (!games.isEmpty),
      "results" -> results, "hasResults" -> (!results.isEmpty))
  }

  def gameToMap(g: Game, stats: Map[String, Population[Team]]): Map[String, Any] = {
    val h = g.homeTeam
    val a = g.awayTeam
    val modelMap: Option[Map[String, Any]] = for (p <- stats.get("point-predictor");
                                                  hv <- p.stat(h);
                                                  av <- p.stat(a)) yield {
      val spread = math.abs(math.round(2 * (hv - av)).toDouble / 2)
      if (hv > av) {
        Map("homeClass" -> "game-fav", "awayClass" -> "game-dog", "spread" -> spread)
      } else {
        Map("homeClass" -> "game-dog", "awayClass" -> "game-fav", "spread" -> spread)
      }
    }
    modelMap.getOrElse(Map.empty[String, Any]) ++ Map("homeTeam" -> Map("key" -> h.key, "name" -> h.name, "record" -> h.record), "awayTeam" -> Map("key" -> a.key, "name" -> a.name, "record" -> a.record))
  }
}
