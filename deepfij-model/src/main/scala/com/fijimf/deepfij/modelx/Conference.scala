package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._

@Entity
@Table(name = "conference",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("schedule_id", "keyName")),
    new UniqueConstraint(columnNames = Array("schedule_id", "name")))
)
class Conference(
                  @(Id@field)
                  @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
                  @(Column@field)(name = "id")
                  val id: Long = 0L,

                  @(ManyToOne@field)(optional = false)
                  @(JoinColumn@field)(name = "schedule_id")
                  val schedule: Schedule,

                  @(Column@field)(name = "keyName")
                  val key: String = "",

                  @(Column@field)(name = "name")
                  val name: String = "",

                  @(OneToMany@field)(mappedBy = "conference", cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY, targetEntity = classOf[Team])
                  val teams: java.util.Set[Team] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]],


                  @(Column@field)(name = "updatedAt")
                  val updatedAt: Date = new Date
                  ) {
  def this() = {
    this(0L, null, "", "", java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]], new Date())
  }

  override def toString() = {
    "Conference( " + id + ", " + schedule.key + ", " + ", " + key + ", " + name + ", " + updatedAt + ")"
  }

  @transient lazy val teamList = teams.toList

  def standings = {
    teamList.sortBy(t => {
      val w = t.conferenceWins.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
      val l = t.conferenceLosses.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
      ((w - l, w))
    }).reverse

  }
}

class ConferenceDao extends BaseDao[Conference, Long] {

  import scala.collection.JavaConversions._

  def findAll(): List[Conference] = entityManager.createQuery("SELECT q FROM Conference q").getResultList.toList.asInstanceOf[List[Conference]]

  def findByKey(k: String): Option[Conference] = {
    try {
      val c: Conference = entityManager.createQuery("SELECT q FROM Conference q where q.schedule.isPrimary=true AND q.key=:key").setParameter("key", k).getSingleResult.asInstanceOf[Conference]
      println(c.name + " " + c.teams.size)
      Some(c)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }
  def findByKey(sk:String, k: String): Option[Conference] = {
    try {
      val c: Conference = entityManager.createQuery("SELECT q FROM Conference q where q.schedule.key=:scheduleKey AND key=:key").setParameter("scheduleKey", sk).setParameter("key", k).getSingleResult.asInstanceOf[Conference]
      println(c.name + " " + c.teams.size)
      Some(c)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }
}

