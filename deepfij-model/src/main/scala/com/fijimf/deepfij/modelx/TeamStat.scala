package com.fijimf.deepfij.modelx

import annotation.target.field
import javax.persistence._
import java.util.Date
import scala.collection.JavaConversions._
import com.fijimf.deepfij.statx._

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
                @(Temporal@field)(value = TemporalType.DATE)
                val date: Date = new Date(),

                @(Column@field)(name = "value")
                val value: Double = 0.0
                ) {
  def this() = this(0L)


}

class TeamStatDao extends BaseDao[TeamStat, Long] {

  def statistic(statKey: String): Statistic[Team] = {
    val stats = entityManager.createQuery("SELECT q FROM TeamStat q where q.team.schedule.isPrimary=true AND q.metaStat.statKey=:statKey")
      .setParameter("statKey", statKey)
      .getResultList.toList.asInstanceOf[List[TeamStat]]
    val parms = entityManager.createQuery("SELECT p FROM StatParameter p where p.metaStat.statKey=:statKey")
      .setParameter("statKey", statKey)
      .getResultList.toList.asInstanceOf[List[StatParameter]]
    resultsToStat(stats, parms)
  }

  def population(statKey: String, date: Date): Population[Team] = {
    val stats = entityManager.createQuery("SELECT q FROM TeamStat q where q.team.schedule.isPrimary=true AND q.metaStat.statKey=:statKey and q.date=:date")
      .setParameter("statKey", statKey)
      .setParameter("date", date)
      .getResultList.toList.asInstanceOf[List[TeamStat]]
    val parms = entityManager.createQuery("SELECT p FROM StatParameter p where p.metaStat.statKey=:statKey AND p.date=:date")
      .setParameter("statKey", statKey)
      .setParameter("date", date)
      .getResultList.toList.asInstanceOf[List[StatParameter]]
    resultsToStat(stats, parms).population(date)
  }

  def timeSeries(statKey: String, teamKey: String): TimeSeries[Team] = {
    val stats = entityManager.createQuery("SELECT q FROM TeamStat q where q.team.schedule.isPrimary=true AND q.metaStat.statKey=:statKey and q.team.key=:teamKey ORDER BY date")
      .setParameter("statKey", statKey)
      .setParameter("teamKey", teamKey)
      .getResultList.toList.asInstanceOf[List[TeamStat]]
    val parms = entityManager.createQuery("SELECT p FROM StatParameter p where p.metaStat.statKey=:statKey")
      .setParameter("statKey", statKey)
      .getResultList.toList.asInstanceOf[List[StatParameter]]
    resultsToStat(stats, parms).series(stats.head.team)
  }

  private[this] def resultsToStat(stats: List[TeamStat], params: List[StatParameter]): Statistic[Team] = {
    require(!stats.isEmpty, "Cannot create stat for empty result")
    val modelKey = stats.head.metaStat.modelKey
    val modelName = stats.head.metaStat.modelName
    val statKey = stats.head.metaStat.statKey
    val name = stats.head.metaStat.name
    val format = stats.head.metaStat.format
    val hib = stats.head.metaStat.higherIsBetter

    val values: Map[(Date, Team), Double] = stats.map(s => (s.date, s.team) -> s.value).toMap
    val parameters: Map[(Date, String), Double] = params.map(p => (p.date, p.name) -> p.value).toMap
    StatisticMap(modelKey, modelName, statKey, name, format, hib, values, parameters)
  }


}
