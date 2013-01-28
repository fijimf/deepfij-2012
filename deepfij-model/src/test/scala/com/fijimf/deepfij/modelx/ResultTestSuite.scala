package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date


@RunWith(classOf[JUnitRunner])
class ResultTestSuite extends DaoTestSuite {

  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val gameDao = new GameDao
  val resultDao = new ResultDao

  test("Create a result") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    val r1 = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val r2 = teamDao.save(new Team(key = "villanova", name = "Villanova", schedule = s, conference = c, longName = "Villanova", updatedAt = new Date))
    val g = gameDao.save(new Game(schedule = s, homeTeam = r1, awayTeam = r2))
    val res = resultDao.save(new Result(game = g, homeScore = 100, awayScore = 32))
    assert(res.id > 0)
  }

  test("Create and retrieve result") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    val r1 = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val r2 = teamDao.save(new Team(key = "villanova", name = "Villanova", schedule = s, conference = c, longName = "Villanova", updatedAt = new Date))
    val g = gameDao.save(new Game(schedule = s, homeTeam = r1, awayTeam = r2))
    val res = resultDao.save(new Result(game = g, homeScore = 100, awayScore = 32))
    assert(res.id > 0)

    val ii = g.id
    val gii = gameDao.findBy(ii)
    val gameList = scheduleDao.findByKey("test").get.gameList
    assert(gii == gameList.headOption)
    assert(gii.get.homeTeam.name == "Georgetown")
    assert(gii.get.awayTeam.name == "Villanova")
    assert(gameList.head.resultOpt.isDefined)
  }

  test("Non-tie requirement") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    val r1 = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val r2 = teamDao.save(new Team(key = "villanova", name = "Villanova", schedule = s, conference = c, longName = "Villanova", updatedAt = new Date))
    val g = gameDao.save(new Game(schedule = s, homeTeam = r1, awayTeam = r2))
    try {
      new Result(game = g, homeScore = 100, awayScore = 100)
      fail("Expected exception")
    } catch {
      case e: IllegalArgumentException => //OK
    }
  }

}