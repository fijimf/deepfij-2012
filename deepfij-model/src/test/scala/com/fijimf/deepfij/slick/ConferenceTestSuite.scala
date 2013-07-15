package com.fijimf.deepfij.slick

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.slick.session.{Database, Session}
import scala.slick.driver.H2Driver.simple._

@RunWith(classOf[JUnitRunner])
class ConferenceTestSuite extends FunSuite with BeforeAndAfter {
  implicit var session: Session = _

  before {
    session = Database.forURL("jdbc:h2:mem:tests", driver = "org.h2.Driver").createSession()
  }

  after {
    session.close()
  }
  test("Conference creation") {
    Conferences.ddl.create
    Conferences.insert(Conference(0, "Big Ten", "Big Ten", None, None, None))
  }
}