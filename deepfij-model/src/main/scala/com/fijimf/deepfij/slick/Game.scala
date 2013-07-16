package com.fijimf.deepfij.slick

import org.joda.time.DateMidnight
import com.fijimf.deepfij.slick.util.DateMidnightMapper._

case class Game(id: Long, seasonId: Long, homeTeamId: Long, awayTeamId: Long, date: DateMidnight, location: Option[String], isNeutralSite: Boolean)

trait GameDao {
  self: Profile =>

  import profile.simple._

  object Games extends Table[Game]("games") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def seasonId = column[Long]("season_id")

    def homeTeamId = column[Long]("homeTeamId")

    def awayTeamId = column[Long]("awayTeamId")

    def date = column[DateMidnight]("date")

    def resultId = column[Long]("result_id")

    def location = column[Option[String]]("location")

    def isNeutralSite = column[Boolean]("isNeutralSite")

    def * = id ~ seasonId ~ homeTeamId ~ awayTeamId ~ date ~ location ~ isNeutralSite <>(Game.apply _, Game.unapply _)
  }

}