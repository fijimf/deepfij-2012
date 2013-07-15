package com.fijimf.deepfij.slick

import scala.slick.driver.H2Driver.simple._

case class Conference(id: Long,
                      name: String,
                      shortName: String,
                      officialUrl: Option[String],
                      officialTwitter: Option[String],
                      logoUrl: Option[String])

object Conferences extends Table[Conference]("conferences") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def shortName = column[String]("shortName")

  def officialUrl = column[Option[String]]("officialUrl")

  def officialTwitter = column[Option[String]]("officialTwitter")

  def logoUrl = column[Option[String]]("logoUrl")

  def * = id ~ name ~ shortName ~ officialUrl ~ officialTwitter ~ logoUrl <> (Conference.apply _, Conference.unapply _)
}

