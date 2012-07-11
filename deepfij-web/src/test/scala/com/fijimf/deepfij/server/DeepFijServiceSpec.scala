package com.fijimf.deepfij.server

import controller.Controller

import java.util.Date
import com.fijimf.deepfij.modelx._

import org.scalatra.test.scalatest._
import org.apache.shiro.util.Factory
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.SecurityUtils
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterEach
import com.fijimf.deepfij.matchers.CustomMatchers


@RunWith(classOf[JUnitRunner])
class DeepFijServiceSpec extends FunSpec with ScalatraSuite with BeforeAndAfterEach with CustomMatchers {
  System.setProperty("deepfij.persistenceUnitName", "deepfij-test")

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
    describe("for the root url") {
      it("should return OK status and a valid page") {
        get("/") {
          status should equal(200)
          body should be(validHtml5)
        }
      }
    }
    describe("for a bad team key") {
      it("should return OK status and a valid page") {
        get("/team/xxxx") {
          status should equal(200)
          body should be(validHtml5)
        }
      }
    }
  }
}
