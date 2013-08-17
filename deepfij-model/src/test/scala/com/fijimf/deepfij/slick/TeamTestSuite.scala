package com.fijimf.deepfij.slick

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver
import java.sql.SQLException

@RunWith(classOf[JUnitRunner])
class TeamTestSuite extends FunSuite with BeforeAndAfter {
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

  test("Team simple creation") {
    val dao: TeamDao = new TeamDao with TestProfile
    dao.Teams.ddl.create

    val i1 = dao.Teams.autoInc.insert("georgetown", "Georgetown","Georgetown", "Hoyas", None, None,None,None, None)
    val i2 = dao.Teams.autoInc.insert("villanova", "Villanova","Villanova", "Wildcats", None, None,None,None, None)
    assert(i1 > 0)
    assert(i2 > 0)
  }

  test("Team simple create retrieve") {
    val dao: TeamDao = new TeamDao with TestProfile
    dao.Teams.ddl.create

    val i1 = dao.Teams.autoInc.insert("georgetown", "Georgetown","Georgetown", "Hoyas", None, None,None,None, None)
    val i2 = dao.Teams.autoInc.insert("villanova", "Villanova","Villanova", "Wildcats", None, None,None,None, None)
    assert(i1 > 0)
    assert(i2 > 0)

    val t: Option[Team] = Query(dao.Teams).filter(_.id === i1).firstOption
    assert(t.isDefined)
    assert(t.get.key == "georgetown")
  }

  test("Team failure blank key") {
    val dao: TeamDao = new TeamDao with TestProfile
    dao.Teams.ddl.create

    val ex = intercept[Exception] {
      val i1 = dao.Teams.autoInc.insert("", "Georgetown","Georgetown", "Hoyas", None, None,None,None, None)

      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

  test("Conference failure null key") {
    val dao: TeamDao = new TeamDao with TestProfile
    dao.Teams.ddl.create

    val ex = intercept[Exception] {
      val i1 = dao.Teams.autoInc.insert(null, "Georgetown","Georgetown", "Hoyas", None, None,None,None, None)

      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

//  test("Conference failure blank name") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("Big East", "", None, None, None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//  }
//
//  test("Conference failure null name") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("Big East", null, None, None, None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//  }
//
//  test("Conference name is unique") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
//      dao.Conferences.autoInc.insert("big-east", "Big Eastx", None, None, None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//
//  }
//
//  test("Conference shortName is unique") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
//      dao.Conferences.autoInc.insert("big-eastx", "Big East", None, None, None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//
//  }
//  test("Conference logoUrl can't be blank") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", None, None, Some(""))
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//
//  }
//  test("Conference officialUrl can't be blank") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", Some(""), None, None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//  }
//
//  test("Conference officialTwitter can't be blank") {
//    val dao: ConferenceDao = new ConferenceDao with TestProfile
//    dao.Conferences.ddl.create
//    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
//
//    val ex = intercept[Exception] {
//      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", None, Some(""), None)
//      fail("Expected exception not thrown")
//    }
//    assert(ex.isInstanceOf[SQLException])
//  }

}