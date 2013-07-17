package com.fijimf.deepfij.slick

import scala.slick.driver.{H2Driver, ExtendedProfile}
import scala.slick.lifted.DDL
import scala.slick.session.{Session, Database}

class Repository(p: ExtendedProfile) {

  trait LocalProfile extends Profile {
    val profile = p
  }


  val Seasons = (new SeasonDao with LocalProfile).Seasons
  val Conferences = (new ConferenceDao with LocalProfile).Conferences
  val Teams = (new TeamDao with LocalProfile).Teams
  val Games = (new GameDao with LocalProfile).Games
  val Results = (new ResultDao with LocalProfile).Results

  val ddl: DDL = Seasons.ddl ++ Conferences.ddl ++ Teams.ddl ++ Games.ddl ++ Results.ddl

}

object Junk {
  def main(args: Array[String]) {
    val repository: Repository = new Repository(H2Driver)

    Database.forURL("jdbc:h2:mem:tests", driver = "org.h2.Driver").withSession({
      val statements: Iterator[String] = repository.ddl.createStatements
      statements.foreach(println(_))
    })

  }
}