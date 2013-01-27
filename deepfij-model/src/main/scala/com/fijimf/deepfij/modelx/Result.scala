package com.fijimf.deepfij.modelx

import javax.persistence._
import annotation.target.field
import java.util.Date

@Entity
@Table(name = "result")
class Result(


              @(Id@field)
              @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
              @(Column@field)(name = "id", nullable = false)
              val id: Long = 0L,

              @(OneToOne@field)
              val game: Game = null,

              @(Column@field)(name = "homeScore")

              var homeScore: Int = 0,
              @(Column@field)(name = "awayScore")
              var awayScore: Int = 0,
              @(Column@field)(name = "updatedAt")
              var updatedAt: Date = new Date
              ) extends KeyedObject {
  def this() = this(0L)

  def winner = if (homeScore > awayScore) {
    game.homeTeam
  } else {
    game.awayTeam
  }

  def loser = if (homeScore < awayScore) {
    game.homeTeam
  } else {
    game.awayTeam
  }

  def key = game.key + ":" + homeScore + ":" + awayScore
}


class ResultDao extends BaseDao[Result, Long] {

}




