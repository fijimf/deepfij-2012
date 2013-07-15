package com.fijimf.deepfij.slick

import scala.slick.driver.H2Driver._
import java.util.Date

case class Result(id:Long, gameId:Long, homeScore:Int, awayScore:Int, numOts:Int)

object Results extends Table[Result]("results") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def gameId = column[Long]("season_id")

  def homeScore = column[Int]("home_score")

  def awayScore = column[Int]("away_score")

  def numOts = column[Int]("num_ots")

  def * = id ~ gameId ~ homeScore ~ awayScore ~ numOts <> (Result.apply _, Result.unapply _)
}
