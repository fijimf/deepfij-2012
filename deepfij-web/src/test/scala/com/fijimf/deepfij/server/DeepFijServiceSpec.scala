package com.fijimf.deepfij.server

import cc.spray.test.SprayTest
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import cc.spray.http._
import com.fijimf.deepfij.modelx.Team._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.modelx.Game._


class DeepFijServiceSpec extends FunSuite with BeforeAndAfterEach with SprayTest with DeepFijService {

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

  def start() {}

  def shutdown() {}

  test("return team missing") {
    val response = testService(HttpRequest(HttpMethods.GET, "/team/xxx")) {
      service
    }.response
    assert(response.status === StatusCodes.OK)
    val s: String = response.content.get.toString
    println(s)
    assert(s.contains("""<div class="alert alert-error">
          The team keyed by the value 'xxx' could not be found.
        </div>"""))
  }
  test("return  a team ") {
    val response = testService(HttpRequest(HttpMethods.GET, "/team/georgetown")) {
      service
    }.response
    assert(response.status === StatusCodes.OK)
    val s: String = response.content.get.toString
    println(s)
    assert(s.contains("""<div class="alert alert-error">
          The team keyed by the value 'xxx' could not be found.
        </div>"""))
  }
}


