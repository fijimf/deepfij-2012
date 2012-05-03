package com.fijimf.deepfij.repo

import com.fijimf.deepfij.modelx._
import java.util.Date
import org.apache.commons.lang.time.DateUtils
import org.apache.log4j.Logger

object TeamData {
  val Key = "key"
  val Name = "name"
  val ConferenceName = "conference"
  val LongName = "longName"
  val Nickname = "nickname"
  val PrimaryColor = "primaryColor"
  val SecondaryColor = "secondaryColor"
  val OfficialUrl = "officialUrl"
  val LogoUrl = "logo"
}

class ScheduleRepository extends Transactional {
  val logger = Logger.getLogger(this.getClass)

  val entityManager = PersistenceSource.entityManager

  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val aliasDao = new AliasDao
  val gameDao = new GameDao
  val resultDao = new ResultDao

  def dropCreateSchedule(key: String, name: String) {
    scheduleDao.findByKey(key).map(s => {
      scheduleDao.delete(s.id)
    })
    scheduleDao.save(new Schedule(key = key, name = name))
  }

  def createConferences(key: String, confNameMap: Map[String, String]) {
    withSchedule(key)(schedule => {
      confNameMap.keys.map(k => {
        logger.info("Saving Conference " + k)
        (k -> conferenceDao.save(new Conference(schedule = schedule, key = k, name = confNameMap(k))))
      })
    })
  }

  def createTeams(key: String, teamData: List[Map[String, String]]) {
    withSchedule(key)(schedule => {
      val confKeyMap = schedule.conferenceList.map(c => (c.key -> c)).toMap
      val confNameMap = schedule.conferenceList.map(c => (c.name -> c)).toMap
      teamData.filter(m => {
        m.contains(TeamData.ConferenceName) && m.contains(TeamData.Name) && m.contains(TeamData.Key)
      }).foreach(m => {
        val conference = confKeyMap.getOrElse(m(TeamData.ConferenceName), confNameMap(m(TeamData.ConferenceName)))
        val longName = m.getOrElse(TeamData.LongName, m(TeamData.Name))
        if (conference != null) {
          logger.info("Saving Team " + m(TeamData.Key))
          teamDao.save(new Team(schedule = schedule, conference = conference, key = m(TeamData.Key), name = m(TeamData.Name), longName = longName, primaryColor = m.getOrElse(TeamData.PrimaryColor, null), secondaryColor = m.getOrElse(TeamData.SecondaryColor, null), officialUrl = m.getOrElse(TeamData.OfficialUrl, null), logo = m.getOrElse(TeamData.LogoUrl, null), nickname = m.getOrElse(TeamData.Nickname, null), updatedAt = new Date()))
        } else {
          println(m(TeamData.ConferenceName) + " not found")
        }
      })
    })
  }

  def createTeamAliases(key: String, aliasMap: Map[String, String]) {
    withSchedule(key)(schedule => {
      val teamMap = schedule.teamList.map(t => (t.key -> t)).toMap
      aliasMap.keys.foreach((s: String) => {
        if (teamMap.contains(aliasMap(s))) {
          logger.info("Saving Alias '" + s + "'")
          aliasDao.save(new Alias(schedule = schedule, team = teamMap(aliasMap(s)), alias = s))
        }
      })
    })
  }

  def createGames(key: String, gameData: List[(Date, String, String)]) {
    withSchedule(key)(schedule => {
      val teamMap = schedule.teamList.map(t => (t.name -> t)).toMap
      val keyMap = schedule.teamList.map(t => (t.key -> t)).toMap
      val aliasMap = schedule.aliasList.map(a => (a.alias -> a.team)).toMap
      gameData.foreach {
        case (date: Date, ht: String, at: String) => {
          val homeTeam = teamMap.get(ht).orElse(keyMap.get(ht).orElse(aliasMap.get(ht)))
          val awayTeam = teamMap.get(at).orElse(keyMap.get(at).orElse(aliasMap.get(at)))
          for (h <- homeTeam; a <- awayTeam) {
            gameDao.save(new Game(schedule = schedule, homeTeam = h, awayTeam = a, date = date))
          }
        }
      }
    })

  }

  def updateResults(key: String, gameData: List[(Date, String, Int, String, Int)]) {
    withSchedule(key)(schedule => {
      val teamMap = schedule.teamList.map(t => {
        t.homeGameList.size
        t.awayGameList.size
        (t.name -> t)
      }).toMap
      val keyMap = schedule.teamList.map(t => (t.key -> t)).toMap
      val aliasMap = schedule.aliasList.map(a => (a.alias -> a.team)).toMap
      gameData.foreach {
        case (date: Date, ht: String, hs: Int, at: String, as: Int) => {
          val homeTeam = teamMap.get(ht).orElse(keyMap.get(ht).orElse(aliasMap.get(ht)))
          val awayTeam = teamMap.get(at).orElse(keyMap.get(at).orElse(aliasMap.get(at)))
          for (h <- homeTeam; a <- awayTeam; g <- h.homeGameList.filter(g => g.awayTeam == a && DateUtils.isSameDay(date, g.date)).headOption) {
            resultDao.save(new Result(game = g, homeScore = hs, awayScore = as))
          }
        }
      }
    })
  }

  def withSchedule(schedKey: String)(f: (Schedule) => Unit) {
    transactional {
      scheduleDao.findByKey(schedKey).map(f(_))
    }
  }
}