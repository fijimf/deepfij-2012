package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils

@Entity
@Table(
  name = "alias",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("scheduleId", "alias"))
  )
) class Alias(

               @(Id@field)
               @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
               @(Column@field)(name = "id", nullable = false)
               val id: Long = 0L,

               @(ManyToOne@field)
               @(JoinColumn@field)(name = "scheduleId")
               val schedule: Schedule = null,

               @(ManyToOne@field)
               @(JoinColumn@field)(name = "teamId")
               val team: Team = null,

               @(Column@field)(name = "alias", nullable = false)
               val alias: String = "",

               @(Column@field)(name = "updatedAt")
               val updatedAt: Date = new Date
               ) {

  require(StringUtils.isBlank(alias) || alias.matches("[a-zA-Z\\-\\.\\'\\&\\,\\(\\) ]+"), "Only a-z A-Z - . ' & , ( ) allowed in team alias.")

  // require((schedule == null && team == null) || (team.schedule.id == schedule.id), "Schedule id and conference schedule id do not match.")

  def this() = this(0L)

  override def toString() = {
    "Alias(" + id + " " + team.name + "-> " + alias + ")"
  }

}

class AliasDao extends BaseDao[Alias, Long] {
  def findAll(): List[Alias] = entityManager.createQuery("SELECT q FROM Alias q").getResultList.toList.asInstanceOf[List[Alias]]
}

