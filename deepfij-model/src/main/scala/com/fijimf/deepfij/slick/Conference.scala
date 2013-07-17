package com.fijimf.deepfij.slick

case class Conference(id: Option[Long],
                      name: String,
                      shortName: String,
                      officialUrl: Option[String],
                      officialTwitter: Option[String],
                      logoUrl: Option[String])

trait ConferenceDao {

  self: Profile =>

  import profile.simple._

  object Conferences extends Table[Conference]("conferences") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def shortName = column[String]("shortName")

    def officialUrl = column[Option[String]]("officialUrl")

    def officialTwitter = column[Option[String]]("officialTwitter")

    def logoUrl = column[Option[String]]("logoUrl")

    def * = id.? ~ name ~ shortName ~ officialUrl ~ officialTwitter ~ logoUrl <>(Conference.apply _, Conference.unapply _)

    val findByName = createFinderBy(_.name)

    val findByShortName = createFinderBy(_.name)

  }


}