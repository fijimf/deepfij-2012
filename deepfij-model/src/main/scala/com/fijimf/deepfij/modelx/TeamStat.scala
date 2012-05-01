package com.fijimf.deepfij.modelx

import annotation.target.field
import javax.persistence._
import java.util.Date
import scala.collection.JavaConversions._

@Entity
@Table(name = "teamStat",
  uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("metaStat_id", "team_id", "date")))
)
class TeamStat(
                @(Id@field)
                @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
                @(Column@field)(name = "id")
                val id: Long = 0L,

                @(ManyToOne@field)(optional = false)
                @(JoinColumn@field)(name = "metaStat_id")
                val metaStat: MetaStat = null,

                @(ManyToOne@field)(optional = false)
                @(JoinColumn@field)(name = "team_id")
                val team: Team = null,

                @(Column@field)(name = "date")
                val date: Date = new Date(),

                @(Column@field)(name = "value")
                val value: Double = 0.0
                ) {
  def this() = this(0L)


}

class TeamStatDao extends BaseDao[TeamStat, Long] {
  def population(key: String, date: Date): List[TeamStat] = {
    entityManager.createQuery("SELECT q FROM TeamStat q where metaStat.key=:key and date=:date")
      .setParameter("key", key)
      .setParameter("date", date)
      .getResultList.toList.asInstanceOf[List[TeamStat]]
  }

  def timeSeries(key: String, teamKey: String): List[TeamStat] = {
    entityManager.createQuery("SELECT q FROM TeamStat q where metaStat.key=:key and team.key=:teamKey ORDER BY date")
      .setParameter("key", key)
      .setParameter("teamKey", teamKey)
      .getResultList.toList.asInstanceOf[List[TeamStat]]
  }

}
