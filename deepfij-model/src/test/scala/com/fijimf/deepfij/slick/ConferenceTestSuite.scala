package com.fijimf.deepfij.slick

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver
import javax.persistence.PersistenceException
import org.hibernate.JDBCException
import org.h2.jdbc.JdbcSQLException
import java.sql.SQLException

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

  test("Conference simple creation") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create

    val i1 = dao.Conferences.autoInc.insert("Big Ten", "Big Ten", None, None, None)
    val i2 = dao.Conferences.autoInc.insert("American Athletic", "American Athletic", None, None, None)
    val i3 = dao.Conferences.autoInc.insert("Big East", "Big East", None, None, None)
    assert(i1 > 0)
    assert(i2 > 0)
    assert(i3 > 0)
  }

  test("Conference failure blank shortName") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.createStatements.foreach(println(_))
    dao.Conferences.ddl.create
    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("", "Big Ten", None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

  test("Conference failure null shortName") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert(null, "American Athletic", None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

  test("Conference failure blank name") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("Big East", "", None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

  test("Conference failure null name") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("Big East", null, None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])
  }

  test("Conference name is unique") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
      dao.Conferences.autoInc.insert("big-east", "Big Eastx", None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])

  }

  test("Conference shortName is unique") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)
      dao.Conferences.autoInc.insert("big-eastx", "Big East", None, None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])

  }
  test("Conference logoUrl can't be blank") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", None, None, Some(""))
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])

  }
  test("Conference officialUrl can't be blank") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", Some(""), None, None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])

  }
  test("Conference officialTwitter can't be blank") {
    val dao: ConferenceDao = new ConferenceDao with TestProfile
    dao.Conferences.ddl.create
    dao.Conferences.autoInc.insert("big-east", "Big East", None, None, None)

    val ex = intercept[Exception] {
      dao.Conferences.autoInc.insert("ACC", "Atlantic Coast Conference", None, Some(""), None)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[SQLException])

  }

}