package com.fijimf.deepfij.modelx


import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class SchemaTestSuite extends DaoTestSuite {

  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val gameDao = new GameDao

  override def beforeEach() {
    super.beforeEach()
    scheduleDao.save(new Schedule(key = "ncaam09", name = "NCAA Men's Basketball 2008-2009"))
    scheduleDao.save(new Schedule(key = "ncaam10", name = "NCAA Men's Basketball 2009-2010"))
    scheduleDao.save(new Schedule(key = "ncaam11", name = "NCAA Men's Basketball 20011"))

  }


  test("Create a schedule") {
    val s: Schedule = new Schedule(key = "ncaam", name = "Men's basketball")
    assert(s.id === 0)

    val r = scheduleDao.save(s)
    assert(r.id > 0)
    assert(r.key === "ncaam")
    assert(r.name === "Men's basketball")
    assert(r.gameList.isEmpty)
    assert(r.conferenceList.isEmpty)
    assert(r.teamList.isEmpty)
  }

  test("Schedule equivalence works") {
    val s: Schedule = new Schedule(key = "ncaam", name = "Men's basketball")
    assert(s.id === 0)

    val r = scheduleDao.save(s)
    assert(r.id > 0)
    assert(r.key === "ncaam")
    assert(r.name === "Men's basketball")
    assert(r.gameList.isEmpty)
    assert(r.conferenceList.isEmpty)
    assert(r.teamList.isEmpty)

    val q = scheduleDao.findBy(r.id).get
    assert(q.id > 0)
    assert(q.key === "ncaam")
    assert(q.name === "Men's basketball")
    assert(q.gameList.isEmpty)
    assert(q.conferenceList.isEmpty)
    assert(q.teamList.isEmpty)

    assert(q.id == r.id)
  }


  test("Schedule name is unique") {
    val s: Schedule = new Schedule(key = "ncaam", name = "Men's basketball")
    val t: Schedule = new Schedule(key = "ncaam", name = "Men's basketball")
    val u = scheduleDao.save(s)
    val ex = intercept[Exception] {
      scheduleDao.save(t)
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[RuntimeException])

    assert(u.id > 0)
  }


  test("Schedule is consistent") {
    val s1 = scheduleDao.save(new Schedule(key = "test1", name = "Test1"))
    val s2 = scheduleDao.save(new Schedule(key = "test2", name = "Test2"))
    val c = conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s1))
    val ex = intercept[Exception] {
      teamDao.save(new Team(key = "georgetown", name = "Georgetown", conference = c, schedule = s2))
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[IllegalArgumentException])

  }

  test("Create a game") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val t = teamDao.save(new Team(key = "seton-hall", name = "Seton Hall", longName = "Seton Hall", conference = c, schedule = s))
    val u = teamDao.save(new Team(key = "cincinatti", name = "Cincinatti", longName = "Cincinatti", conference = c, schedule = s))
    assert(t.id != 0)
    assert(u.id != 0)

    val g: Game = gameDao.save(new Game(homeTeam = u, awayTeam = t, schedule = s))
    assert(g != null)
    assert(g.id > 0)
    assert(g.homeTeam.id == u.id)
    assert(g.awayTeam.id == t.id)

    val u1 = teamDao.findBy(u.id).get
    teamDao.findBy(t.id).get
    assert(u1.games.size == 1)
    assert(u1.homeGames.size == 1)
    assert(u1.awayGames.size == 0)
  }

}