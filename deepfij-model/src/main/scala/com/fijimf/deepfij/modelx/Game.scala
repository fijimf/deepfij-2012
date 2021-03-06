package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._
import java.text.SimpleDateFormat

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
            @(Temporal@field)(value = TemporalType.DATE)
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
            ) extends KeyedObject {
  require((homeTeam == null && awayTeam == null && homeTeam == null) || (homeTeam != null && awayTeam != null && schedule != null))

  def this() = this(0L)

  @transient lazy val resultOpt = Option(result)

  def isWin(t: Team): Boolean = {
    resultOpt.isDefined && t == winner.get
  }

  def isLoss(t: Team): Boolean = {
    resultOpt.isDefined && t == loser.get
  }

  def winner: Option[Team] = {
    resultOpt.map(_.winner)
  }

  def loser: Option[Team] = {
    resultOpt.map(_.loser)
  }

  def key = {
    new SimpleDateFormat("yyyyMMdd").format(date) + ":" + homeTeam.key + ":" + awayTeam.key
  }
}

class GameDao extends BaseDao[Game, Long] {

  def findAll(): List[Game] = entityManager.createQuery("SELECT q FROM Game q").getResultList.toList.asInstanceOf[List[Game]]

}

