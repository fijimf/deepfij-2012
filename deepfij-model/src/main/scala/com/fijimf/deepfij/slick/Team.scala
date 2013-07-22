package com.fijimf.deepfij.slick


case class Team(id: Long, seasonId: Long, key: String, name: String, longName: String, nickname: String, primaryColor: Option[String], secondaryColor: Option[String], logoUrl: Option[String], officialUrl: Option[String], officialTwitter: Option[String])

trait TeamDao {

  this: Profile =>

  import profile.simple._

  object Teams extends Table[Team]("teams") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def seasonId = column[Long]("season_id")

    def key = column[String]("short_name")

    def name = column[String]("name")

    def longName = column[String]("long_name")

    def nickname = column[String]("nickname")

    def primaryColor = column[Option[String]]("primary_color")

    def secondaryColor = column[Option[String]]("secondary_color")

    def logoUrl = column[Option[String]]("logo_url")

    def officialUrl = column[Option[String]]("official_url")

    def officialTwitter = column[Option[String]]("official_twitter")

    def * = id ~ seasonId ~ key ~ name ~ longName ~ nickname ~ primaryColor ~ secondaryColor ~ logoUrl ~ officialUrl ~ officialTwitter <>(Team.apply _, Team.unapply _)

    def autoInc = seasonId ~ key ~ name ~ longName ~ nickname ~ primaryColor ~ secondaryColor ~ logoUrl ~ officialUrl ~ officialTwitter returning id
  }

}