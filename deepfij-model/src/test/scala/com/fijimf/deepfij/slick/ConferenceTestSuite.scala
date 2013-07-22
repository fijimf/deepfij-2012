package com.fijimf.deepfij.slick

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.slick.session.{Database, Session}
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

@RunWith(classOf[JUnitRunner])
class ConferenceTestSuite extends FunSuite with BeforeAndAfter {
  implicit var session: Session = _

  trait TestProfile extends Profile {
    val profile = H2Driver
  }

  before {
    session = Database.forURL("jdbc:h2:mem:tests", driver = "org.h2.Driver").createSession()
  }

  after {
    session.close()
  }
  test("Conference creation") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    dao.Conferences.autoInc.insert("Big Ten", "Big Ten", None, None, None)
    dao.Conferences.autoInc.insert( "American Athletic", "American Athletic", None, None, None)
    dao.Conferences.autoInc.insert( "Big East", "Big East", None, None, None)
  }
}