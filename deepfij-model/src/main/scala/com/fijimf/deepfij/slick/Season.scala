package com.fijimf.deepfij.slick

import scala.slick.driver.H2Driver.simple._

case class Season(id:Long, year: String)

object Seasons extends Table[Season]("seasons") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def year = column[String]("name")

  def * = id ~ year  <> (Season.apply _, Season.unapply _)

}