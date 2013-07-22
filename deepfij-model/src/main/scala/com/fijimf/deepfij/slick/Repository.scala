package com.fijimf.deepfij.slick

import scala.slick.driver.{H2Driver, ExtendedProfile}
import scala.slick.lifted.DDL
import scala.slick.session.{Database, Session}
import scala.slick.session.Database._

class Repository(p: ExtendedProfile) extends SeasonDao with ConferenceDao with TeamDao with GameDao with ResultDao with Profile {

  val profile = p

  import profile.simple._

  val ddl: DDL = Seasons.ddl ++ Conferences.ddl ++ Teams.ddl ++ Games.ddl ++ Results.ddl

  def create = ddl.create
  def drop = ddl.drop

  def newSeason(year:String):Long = {
     Seasons.autoInc.insert(year)
  }

  def listSeasons() {
    val q = for (s <- Seasons) yield (s)
    q.foreach(println(_))
  }
}

object Junk {
  def main(args: Array[String]) {
    val driver: H2Driver.type = H2Driver
    val repository: Repository = new Repository(driver)
    import driver.simple._

    val db: Database = forURL("jdbc:h2:mem:tests", driver = "org.h2.Driver")
    db withSession {
      (repository.ddl).create

      repository.newSeason("2013")
      repository.listSeasons()
    }

  }
}