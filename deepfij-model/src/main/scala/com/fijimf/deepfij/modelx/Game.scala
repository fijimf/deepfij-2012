package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._

@Entity
@Table(name = "game")
class Game(


            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(ManyToOne@field)
            val schedule: Schedule = null,

            @(ManyToOne@field)
            @(JoinColumn@field)(name = "homeTeamId")
            val homeTeam: Team = null,
            @(ManyToOne@field)
            @(JoinColumn@field)(name = "awayTeamId")
            val awayTeam: Team = null,

            @(Column@field)(name = "date", nullable = false)
            @(Temporal@field)(value=TemporalType.DATE)
            val date: Date = new Date,

            @(Column@field)(name = "isNeutralSite", nullable = false)
            var isNeutralSite: Boolean = false,
            @(Column@field)(name = "isConferenceTournament", nullable = false)
            var isConferenceTournament: Boolean = false,
            @(Column@field)(name = "isNcaaTournament", nullable = false)
            val isNcaaTournament: Boolean = false,

            @(OneToOne@field)(fetch = FetchType.EAGER, mappedBy = "game", cascade = Array(CascadeType.REMOVE))
            val result: Result = null,

            @(Column@field)(name = "updatedAt")
            var updatedAt: Date = new Date
            ) {
  require((homeTeam == null && awayTeam == null) || (homeTeam != null))

  def this() = this(0L)

  @transient lazy val resultOpt = Option(result)

  def isWin(t: Team): Boolean = {
    resultOpt.isDefined && ((homeTeam == t && result.homeWin) || (awayTeam == t && result.homeLoss))
  }

  def isLoss(t: Team): Boolean = {
    resultOpt.isDefined && ((homeTeam == t && result.homeLoss) || (awayTeam == t && result.homeWin))
  }

  def winner: Option[Team] = {
    resultOpt.map(r => if (r.homeWin) homeTeam else awayTeam)
  }

  def loser: Option[Team] = {
    resultOpt.map(r => if (r.homeLoss) homeTeam else awayTeam)
  }
}

class GameDao extends BaseDao[Game, Long] {

  def findAll(): List[Game] = entityManager.createQuery("SELECT q FROM Game q").getResultList.toList.asInstanceOf[List[Game]]

}

