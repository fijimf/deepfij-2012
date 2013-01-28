package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date
import java.text.SimpleDateFormat

@RunWith(classOf[JUnitRunner])
class GameTestSuite extends DaoTestSuite {
  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val tdao: TeamDao = new TeamDao
  val dao: GameDao = new GameDao
  val rao: ResultDao = new ResultDao

  test("Create requirements") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val q = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val r = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    try {
      new Game(schedule = s, homeTeam = q)
      fail("Expected exception not thrown")
    }
    catch {
      case _: Throwable => //OK
    }
    try {
      new Game(homeTeam = q, awayTeam = r)
      fail("Expected exception not thrown")
    }
    catch {
      case _: Throwable => //OK
    }
    try {
      new Game(schedule = s, awayTeam = r)
      fail("Expected exception not thrown")
    }
    catch {
      case _: Throwable => //OK
    }
    try {
      new Game(schedule = s, homeTeam = r, awayTeam = r)
      fail("Expected exception not thrown")
    }
    catch {
      case _: Throwable => //OK
    }

  }

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
    dao.save(new Game(schedule = s, homeTeam = q, awayTeam = r))

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

  test("Game data methods") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    val q = tdao.save(new Team(key = "marquette", name = "Marquette", schedule = s, conference = c, longName = "Marquette", updatedAt = new Date))
    val r = tdao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val x = tdao.save(new Team(key = "cincinatti", name = "Cincinatti", schedule = s, conference = c, longName = "Cincinatti", updatedAt = new Date))
    val gg = dao.save(new Game(schedule = s, homeTeam = q, awayTeam = r))

    assert(!gg.isLoss(q))
    assert(!gg.isLoss(r))
    assert(!gg.isWin(q))
    assert(!gg.isWin(r))
    assert(!gg.isConferenceTournament)
    assert(!gg.isNcaaTournament)
    assert(!gg.isNeutralSite)
    assert(gg.resultOpt.isEmpty)
    assert(gg.key == new SimpleDateFormat("yyyyMMdd").format(new Date) + ":marquette:georgetown")

    rao.save(new Result(game = gg, homeScore = 99, awayScore = 101))

    val hh = dao.findBy(gg.id).get
    val mu: Team = hh.homeTeam
    val gu: Team = hh.awayTeam

    assert(hh.isLoss(mu))
    assert(!hh.isLoss(gu))
    assert(!hh.isLoss(x))
    assert(!hh.isWin(mu))
    assert(hh.isWin(gu))
    assert(!hh.isWin(x))

    assert(!hh.resultOpt.isEmpty)
  }
}