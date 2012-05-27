package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils

@Entity
@Table(
  name = "team",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("schedule_id", "keyName")),
    new UniqueConstraint(columnNames = Array("schedule_id", "name")),
    new UniqueConstraint(columnNames = Array("schedule_id", "longName"))
  )
) class Team(

              @(Id@field)
              @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
              @(Column@field)(name = "id", nullable = false)
              val id: Long = 0L,

              @(ManyToOne@field)(cascade = Array(CascadeType.ALL))
              @(JoinColumn@field)(name = "schedule_id")
              val schedule: Schedule = null,

              @(ManyToOne@field)(cascade = Array(CascadeType.ALL))
              @(JoinColumn@field)(name = "conference_id")
              val conference: Conference = null,

              @(Column@field)(name = "keyName", nullable = false)
              val key: String = "",

              @(Column@field)(name = "name", nullable = false)
              var name: String = "",

              @(Column@field)(name = "longName", nullable = false)
              var longName: String = "",

              @(Column@field)(name = "nickname", nullable = true)
              var nickname: String = null,

              @(Column@field)(name = "primaryColor", nullable = true)
              var primaryColor: String = null,

              @(Column@field)(name = "secondaryColor", nullable = true)
              var secondaryColor: String = null,

              @(Column@field)(name = "logo", nullable = true)
              var logo: String = null,

              @(Column@field)(name = "officialUrl", nullable = true)
              var officialUrl: String = null,

              @(Column@field)(name = "updatedAt")
              var updatedAt: Date = new Date,

              @(OneToMany@field)(mappedBy = "homeTeam", fetch = FetchType.LAZY)
              val homeGames: java.util.Set[Game] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]],

              @(OneToMany@field)(mappedBy = "awayTeam", fetch = FetchType.LAZY)
              val awayGames: java.util.Set[Game] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]]

              ) {
  def this() = this(0L)

  require(StringUtils.isBlank(key) || key.matches("[a-z\\-]+"), "Only a-z and - allowed in team keys.")
  require(StringUtils.isBlank(name) || name.matches("[a-zA-Z\\-\\.\\'\\&\\,\\(\\) ]+"), "Only a-z A-Z - . ' & , ( ) allowed in team names.")
  require(StringUtils.isBlank(key) == StringUtils.isBlank(name), "Key can be blank if and only if name is blank")
  require((schedule == null && conference == null) || (conference.schedule.id == schedule.id), "Schedule id and conference schedule id do not match.")

  override def toString() = {
    "Team(" + id + ", " + key + ", " + name + ", " + conference.name + ", " + schedule.name + ")"
  }

  def nicknameOpt = Option(nickname)
  def primaryColorOpt = Option(primaryColor)
  def secondaryColorOpt = Option(secondaryColor)
  def logoOpt = Option(logo)
  def officialUrlOpt = Option(officialUrl)
  @transient lazy val homeGameList = homeGames.toList
  @transient lazy val awayGameList = awayGames.toList

  def games = homeGameList ::: awayGameList

  def conferenceGames = games.filter(g => (g.homeTeam.conference == g.awayTeam.conference))

  def nonConferenceGames = games.filter(g => (g.homeTeam.conference != g.awayTeam.conference))

  def wins = games.filter(_.isWin(this))

  def losses = games.filter(_.isLoss(this))

  def conferenceWins = conferenceGames.filter(_.isWin(this))

  def conferenceLosses = conferenceGames.filter(_.isLoss(this))
}

class TeamDao extends BaseDao[Team, Long] {
  def findAll(): List[Team] = entityManager.createQuery("SELECT q FROM Team q").getResultList.toList.asInstanceOf[List[Team]]

  def findByKey(k: String): Option[Team] = {
    try {
      val t: Team = entityManager.createQuery("SELECT q FROM Team q WHERE q.schedule.isPrimary=true AND key=:key").setParameter("key", k).getSingleResult.asInstanceOf[Team]
      println(t.name + " " +
        t.conference.name + " " +
        t.conferenceGames.size + " " +
        t.wins.size + " " +
        t.losses.size + " ")
      Some(t)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }

  def findByKey(sk: String, k: String): Option[Team] = {
    try {
      val t: Team = entityManager.createQuery("SELECT q FROM Team q WHERE q.schedule.key=:scheduleKey AND q.key=:key").setParameter("key", k).setParameter("scheduleKey", sk).getSingleResult.asInstanceOf[Team]
      println(t.name + " " +
        t.conference.name + " " +
        t.conferenceGames.size + " " +
        t.wins.size + " " +
        t.losses.size + " ")
      Some(t)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }

  def search(q: String): List[Team] = {
    val ts: List[Team] = entityManager.createQuery("SELECT q FROM Team q where name LIKE :key or keyName LIKE :key").setParameter("key", "%" + q + "%").getResultList.toList.asInstanceOf[List[Team]]
    ts.foreach(t => {
      println(t.name + " " +
        t.conference.name + " " +
        t.conferenceGames.size + " " +
        t.wins.size + " " +
        t.losses.size + " ")
    })

    ts

  }
}

