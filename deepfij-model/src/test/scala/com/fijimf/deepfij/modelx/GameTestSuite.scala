package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class GameTestSuite extends DaoTestSuite {
  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val tdao: TeamDao = new TeamDao
  val dao: GameDao = new GameDao

  test("Save a game") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val q = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val r = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val gg = dao.save(new Game(schedule = s, homeTeam = q, awayTeam = r))
    assert(gg.id > 0)
  }

  test("Find games") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val q = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val r = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val gg = dao.save(new Game(schedule = s, homeTeam = q, awayTeam = r))

    assert(dao.findAll().size == 1)
    val g1 = dao.findAll().head
    assert(g1 != null)
    assert(g1.homeTeam.key == q.key)
    assert(g1.awayTeam.key == r.key)
    assert(g1.homeTeam.homeGames.size == 1)
    assert(g1.awayTeam.awayGames.size == 1)
    assert(g1.schedule.teamList.size == 2)
    assert(g1.schedule.gameList.size == 1)
    assert(g1.resultOpt == None)
  }
}