package com.fijimf.deepfij.server

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import java.util.Date
import com.fijimf.deepfij.modelx._

import org.scalatra.test.scalatest._
import org.eclipse.jetty.http.HttpMethods
import org.scalatra.test.ScalatraTests


class DeepFijServiceSpec extends ScalatraTests with ScalatraFunSuite with BeforeAndAfterEach {

  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val tdao: TeamDao = new TeamDao
  val dao: GameDao = new GameDao

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()

    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val q = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val r = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val gg = dao.save(new Game(schedule = s, homeTeam = q, awayTeam = r))
  }

  def activeScheduleKey() = "test"


  //
  //  test("Return team missing for a missing key") {
  //    val response = tester.HttpRequest(HttpMethods.GET, "/team/xxx")) {
  //      service
  //    }.response
  //    assert(response.status === StatusCodes.OK)
  //    val s: String = response.content.get.toString
  //    assert(s.contains("""<div class="alert alert-error">
  //          The team keyed by the value 'xxx' could not be found.
  //        </div>"""))
  //  }
  //
  //  test("Return a team page for a valid key") {
  //    val response = testService(HttpRequest(HttpMethods.GET, "/team/georgetown")) {
  //      service
  //    }.response
  //    val s: String = response.content.get.toString
  //    assert(response.status === StatusCodes.OK)
  //    assert(s.contains("""      <div class="span11">
  //        <h1>
  //          Georgetown  (0-0, 0-0)
  //        </h1>
  //        <h3>
  //          <a href="/conference/big-east">Big East</a>
  //        </h3>
  //      </div>"""))
  //  }
  //
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


