package com.fijimf.deepfij.server

import controller.Controller
import org.scalatest.{FunSpec, BeforeAndAfterEach}
import java.util.Date
import com.fijimf.deepfij.modelx._

import org.scalatra.test.scalatest._
import org.apache.shiro.util.Factory
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.SecurityUtils
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class DeepFijServiceSpec extends FunSpec with ScalatraSuite with BeforeAndAfterEach {

  val factory: Factory[SecurityManager] = new IniSecurityManagerFactory("classpath:shiro.ini")
  val securityManager: SecurityManager = factory.getInstance();

  SecurityUtils.setSecurityManager(securityManager)
  addFilter(classOf[Controller], "/*")

  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val tdao: TeamDao = new TeamDao
  val gdao: GameDao = new GameDao
  val rdao: ResultDao = new ResultDao

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()

    val s = sdao.save(new Schedule(0L, "test", "Test", isPrimary = true))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val ta = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val tb = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val tc = tdao.save(new Team(key = "syracuse", name = "Syracuse", schedule = s, conference = c, longName = "Syracuse", updatedAt = new Date))
    val ga = gdao.save(new Game(schedule = s, homeTeam = ta, awayTeam = tb))
    val gb = gdao.save(new Game(schedule = s, homeTeam = tc, awayTeam = tb))
    val res = rdao.save(new Result(game = gb, homeScore = 50, awayScore = 123))
  }

  describe("The DeepfijController filter") {
    describe("for an unknown team") {
      it("should return status OK") {
        get("/team/xxx") {
          status should equal(200)
        }
      }
      it("should have the valid error message") {
        get("/team/xxx") {
          body should include(
            """The team keyed by the value 'xxx' could not be found."""
          )
        }
      }
    }
    describe("for a known team") {
      get("/team/georgetown") {
        it("should return status OK") {
          status should equal(200)
        }
        it("should have a heading with the team's short name and the record") {
          body should include regex ("""<h1>(/s+)Georgetown  \(1-0, 1-0\)(\s+)</h1>""")
        }
        it("display a link to the conference page") {
          body should include regex ("""<a href="/conference/big-east">(\s+)Big East(\s+)</a>""")
        }
      }
    }
  }
  //  test("Return a confernece for a valid key") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/conference/big-east")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //    val s: String = response.content.get.toString
  //    assert(s.contains("""<div class="span12">
  //        <h1>
  //          Big East
  //        </h1>
  //      </div>"""))
  //
  //  }
  //  test("Return conference missing for a missing conference key") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/conference/zzz")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //    val s: String = response.content.get.toString
  //    assert(s.contains("""<div class="alert alert-error">
  //          The conference keyed by the value 'zzz' could not be found.
  //        </div>"""))
  //  }
  //  test("Return a quote") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/quote")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //  }
  //  test("Return the login screen") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/login")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //  }
  //  test("Search") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/search?q=geo")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //  }
  //  test("Return a date panel") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/date/20120401")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //  }
  //  test("Return admin screen") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/admin")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //  }
}


