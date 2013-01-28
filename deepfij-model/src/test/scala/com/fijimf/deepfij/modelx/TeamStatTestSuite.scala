package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date
import java.text.SimpleDateFormat

@RunWith(classOf[JUnitRunner])
class TeamStatTestSuite extends DaoTestSuite {
  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val metaStatDao = new MetaStatDao
  val teamStatDao = new TeamStatDao

  test("Create") {

    val s = scheduleDao.save(new Schedule(0L, "test", "Test"))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val r = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(statKey = "wins", name = "Wins", higherIsBetter = true))

    val tst = teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = new Date(), value = 28.0))
    assert(tst.id > 0)
  }

  test("Find series") {
    val fmt = new SimpleDateFormat("MM/dd/yyyy")

    val jan01: Date = fmt.parse("01/01/2011")
    val jan02: Date = fmt.parse("01/02/2011")
    val jan03: Date = fmt.parse("01/03/2011")
    val s = scheduleDao.save(new Schedule(0L, "test", "Test", true))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val r = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(statKey = "wins", name = "Wins", higherIsBetter = true))

    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan01, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan02, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan03, value = 29.0))

    val series = teamStatDao.timeSeries("wins", "georgetown")
    assert(series.startDate === jan01)
    assert(series.endDate === jan03)
  }

  test("Find population") {
    val fmt = new SimpleDateFormat("MM/dd/yyyy")

    val jan01: Date = fmt.parse("01/01/2011")
    val s = scheduleDao.save(new Schedule(0L, "test", "Test", true))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val rr = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val rs = teamDao.save(new Team(key = "villanova", name = "Villanova", schedule = s, conference = c, longName = "Villanova", updatedAt = new Date))
    val rt = teamDao.save(new Team(key = "syracuse", name = "Syracuse", schedule = s, conference = c, longName = "Syracuse", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(statKey = "wins", name = "Wins", higherIsBetter = true))

    teamStatDao.save(new TeamStat(metaStat = mst, team = rr, date = jan01, value = 38.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = rs, date = jan01, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = rt, date = jan01, value = 29.0))

    val pop = teamStatDao.population("wins", jan01)
    assert(pop.count === 3)
    assert(pop.max === Some(38.0))
    assert(pop.min === Some(28.0))
  }
}