package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import com.fijimf.deepfij.util.Validation._
import scala.Some

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
                  var name: String = "",

                  @(OneToMany@field)(mappedBy = "conference", cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY, targetEntity = classOf[Team])
                  val teams: java.util.Set[Team] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]],


                  @(Column@field)(name = "updatedAt")
                  var updatedAt: Date = new Date
                  ) extends KeyedObject {
  require(StringUtils.isBlank(key) || validKey(key), "Only a-z and - allowed in conference keys.")
  require(StringUtils.isBlank(name) || validName(name), "Only a-z A-Z - . ' & , ( ) allowed in conference names.")
  require(StringUtils.isBlank(key) == StringUtils.isBlank(name), "Key can be blank if and only if name is blank")

  def this() = {
    this(0L, null, "", "", java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Team]], new Date())
  }

  override def toString = {
    "Conference( " + id + ", " + schedule.key + ", " + ", " + key + ", " + name + ", " + updatedAt + ")"
  }

  @transient lazy val teamList = teams.toList

  def standings = {
    teamList.sortBy(t => {
      val w = t.wins.size
      val cw = t.conferenceWins.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
      val l = t.losses.size
      val cl = t.conferenceLosses.filter(g => (!g.isConferenceTournament && !g.isNcaaTournament)).size
      ((cw - cl, cw, w - l, l))
    }).reverse

  }
}

class ConferenceDao extends BaseDao[Conference, Long] with KeyedObjectDao[Conference] {

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

  def findByKey(sk: String, k: String): Option[Conference] = {
    try {
      val c: Conference = entityManager.createQuery("SELECT q FROM Conference q where q.schedule.key=:scheduleKey AND key=:key").setParameter("scheduleKey", sk).setParameter("key", k).getSingleResult.asInstanceOf[Conference]
      // println(c.name + " " + c.teams.size)
      Some(c)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }
}

