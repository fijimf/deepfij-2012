package com.fijimf.deepfij.slick


case class Result(id: Long, gameId: Long, homeScore: Int, awayScore: Int, numOts: Int)

trait ResultDao {
  self: Profile =>
  import profile.simple._

  object Results extends Table[Result]("results") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def gameId = column[Long]("season_id")

    def homeScore = column[Int]("home_score")

    def awayScore = column[Int]("away_score")

    def numOts = column[Int]("num_ots")

    def * = id ~ gameId ~ homeScore ~ awayScore ~ numOts <>(Result.apply _, Result.unapply _)
  }

}