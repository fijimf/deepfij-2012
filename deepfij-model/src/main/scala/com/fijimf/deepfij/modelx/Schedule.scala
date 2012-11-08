package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import com.fijimf.deepfij.util.Validation._

@Entity
@Table(name = "schedule")
class Schedule(

                @(Id@field)
                @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
                @(Column@field)(name = "id")
                val id: Long = 0L,

                @(Column@field)(name = "keyName", unique = true)
                val key: String = "",

                @(Column@field)(name = "name", unique = true)
                var name: String = "",

                @(Column@field)(name = "isPrimary")
                var isPrimary: Boolean = false,

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.EAGER, targetEntity = classOf[Conference])
                val conferences: java.util.Set[Conference] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Conference]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.EAGER, targetEntity = classOf[Team])
                val teams: java.util.Set[Team] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.EAGER, targetEntity = classOf[Game])
                val games: java.util.Set[Game] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.EAGER, targetEntity = classOf[Alias])
                val aliases: java.util.Set[Alias] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Alias]],

                @(Column@field)(name = "updatedAt")
                val updatedAt: Date = new Date
                ) {
  require(StringUtils.isBlank(key) || validKey(key), "Only 0-9, a-z and - allowed in schedule keys.")
  require(StringUtils.isBlank(name) || validName(name), "Only a-z A-Z - . ' & , ( ) allowed in schedule names.")
  require(StringUtils.isBlank(key) == StringUtils.isBlank(name), "Key can be blank if and only if name is blank")

  @transient lazy val conferenceList: List[Conference] = conferences.toList
  @transient lazy val conferenceByKey: Map[String, Conference] = conferenceList.map(c => c.key -> c).toMap
  @transient lazy val conferenceByName: Map[String, Conference] = conferenceList.map(c => c.name -> c).toMap
  @transient lazy val teamList: List[Team] = teams.toList
  @transient lazy val teamByKey: Map[String, Team] = teamList.map(t => t.key -> t).toMap
  @transient lazy val gameList: List[Game] = games.toList
  @transient lazy val gameByKey: Map[String, Game] = gameList.map(g => g.key -> g).toMap
  @transient lazy val aliasList: List[Alias] = aliases.toList
  @transient lazy val aliasByKey: Map[String, Alias] = aliasList.map(a => a.key -> a).toMap

  def this() = {
    this(0L, "", "", false, java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Conference]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Alias]], new Date())
  }

  def vitalsSigns() = Map(
    "name" -> name, "key" -> key,
    "numberOfConferences" -> conferenceList.size,
    "conferenceSize" ->
      conferenceList.map(c => (c.key -> c.teamList.size)).toMap,
    "numberOfTeams" -> teamList.size,
    "teamGames" -> teamList.map(t => (t.key -> Map("first" -> t.games.minBy(_.date), "last" -> t.games.maxBy(_.date), "count" -> t.games.size)))
  )
}

class ScheduleDao extends BaseDao[Schedule, Long] {

  def findByKey(key: String): Option[Schedule] = {
    val s: Option[Schedule] = entityManager.createQuery("SELECT s FROM Schedule s WHERE s.key = :key").setParameter("key", key).getResultList.toList.asInstanceOf[List[Schedule]].headOption
    s.map(sch => {
      sch.conferenceList; sch.teamList; sch.gameList; sch.aliasList; sch
    })
  }

  def findAll(): List[Schedule] = entityManager.createQuery("SELECT s FROM Schedule s").getResultList.toList.asInstanceOf[List[Schedule]]

  def findPrimary(): Option[Schedule] = {
    entityManager.createQuery("SELECT s FROM Schedule s WHERE s.isPrimary = TRUE").getResultList.toList.asInstanceOf[List[Schedule]].headOption
  }

  def setPrimary(key: String) = {
    findAll().map(s => {
      s.isPrimary = (s.key == key)
      save(s)
    })
  }

}

