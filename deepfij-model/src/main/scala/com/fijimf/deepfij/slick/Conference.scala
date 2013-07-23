package com.fijimf.deepfij.slick

import org.apache.commons.lang.StringUtils

case class Conference(id: Long,
                      name: String,
                      shortName: String,
                      officialUrl: Option[String],
                      officialTwitter: Option[String],
                      logoUrl: Option[String]) {
  require(StringUtils.isNotBlank(name), "Name cannot be blank")
  require(StringUtils.isNotBlank(shortName), "Short name cannot be blank")
  require(officialUrl.map(StringUtils.isNotBlank).getOrElse(true), "Official URL cannot be blank")
  require(officialTwitter.map(StringUtils.isNotBlank).getOrElse(true), "Official twitter cannot be blank")
  require(logoUrl.map(StringUtils.isNotBlank).getOrElse(true), "logo URL cannot be blank")

}

trait ConferenceDao {

  self: Profile =>

  import profile.simple._

  object Conferences extends Table[Conference]("conferences") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def shortName = column[String]("short_name")

    def officialUrl = column[Option[String]]("official_url")

    def officialTwitter = column[Option[String]]("official_twitter")

    def logoUrl = column[Option[String]]("logo_url")

    def * = id ~ name ~ shortName ~ officialUrl ~ officialTwitter ~ logoUrl <>(Conference.apply _, Conference.unapply _)

    def autoInc = name ~ shortName ~ officialUrl ~ officialTwitter ~ logoUrl returning id

  }


}