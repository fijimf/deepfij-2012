package com.fijimf.deepfij.slick

case class Season(id: Long, year: String)

trait SeasonDao {
  this: Profile =>
  import profile.simple._

  object Seasons extends Table[Season]("seasons") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def year = column[String]("name")

    def * = id ~ year <>(Season.apply _, Season.unapply _)
  }

}