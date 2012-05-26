package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils

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
                val name: String = "",

                @(Column@field)(name = "isPrimary")
                val isPrimary: Boolean = false,

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[Conference])
                val conferences: java.util.Set[Conference] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Conference]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[Team])
                val teams: java.util.Set[Team] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[Game])
                val games: java.util.Set[Game] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]],

                @(OneToMany@field)(mappedBy = "schedule", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[Alias])
                val aliases: java.util.Set[Alias] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Alias]],

                @(Column@field)(name = "updatedAt")
                val updatedAt: Date = new Date
                ) {
  require(StringUtils.isBlank(key) || key.matches("[0-9a-z\\-]+"), "Only 0-9, a-z and - allowed in team keys.")
  require(StringUtils.isBlank(name) || name.matches("[0-9a-zA-Z\\-\\.\\'\\&\\,\\(\\) ]+"), "Only a-z A-Z - . ' & , ( ) allowed in team names.")
  require(StringUtils.isBlank(key) == StringUtils.isBlank(name), "Key can be blank if and only if name is blank")

  @transient lazy val conferenceList: List[Conference] = conferences.toList
  @transient lazy val teamList: List[Team] = teams.toList
  @transient lazy val gameList: List[Game] = games.toList
  @transient lazy val aliasList: List[Alias] = aliases.toList

  def this() = {
    this(0L, "", "", false, java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Conference]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Game]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Alias]], new Date())
  }
}

class ScheduleDao extends BaseDao[Schedule, Long] {

  def findByKey(key: String): Option[Schedule] = {
    entityManager.createQuery("SELECT s FROM Schedule s WHERE s.key = :key").setParameter("key", key).getResultList.toList.asInstanceOf[List[Schedule]].headOption
  }

  def findAll(): List[Schedule] = entityManager.createQuery("SELECT s FROM Schedule s").getResultList.toList.asInstanceOf[List[Schedule]]

  def setPrimary(key: String) = {
    transactional {
      entityManager.createQuery("UPDATE Schedule s SET s.isPrimary = false WHERE s.key != :key").setParameter("key", key).executeUpdate()
      entityManager.createQuery("UPDATE Schedule s SET s.isPrimary = true WHERE s.key = :key").setParameter("key", key).executeUpdate()
    }
    entityManager.flush()
  }

}

