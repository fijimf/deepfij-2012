package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import java.util.Date
import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.Team._
import com.fijimf.deepfij.modelx.Result._

@RunWith(classOf[JUnitRunner])
class TeamStatTestSuite extends FunSuite with BeforeAndAfterEach {
  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val metaStatDao = new MetaStatDao
  val teamStatDao = new TeamStatDao


  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
  }


  test("Create") {

    val s = scheduleDao.save(new Schedule(0L, "test", "Test"))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val r = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(key = "wins", name = "Wins", higherIsBetter = true))

    val tst = teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = new Date(), value = 28.0))
    assert(tst.id > 0)
  }

  test("Find series") {
    val fmt = new SimpleDateFormat("MM/dd/yyyy")

    val jan01: Date = fmt.parse("01/01/2011")
    val jan02: Date = fmt.parse("01/02/2011")
    val jan03: Date = fmt.parse("01/03/2011")
    val s = scheduleDao.save(new Schedule(0L, "test", "Test"))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val r = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(key = "wins", name = "Wins", higherIsBetter = true))

    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan01, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan02, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = r, date = jan03, value = 29.0))

    val series: List[TeamStat] = teamStatDao.timeSeries("wins", "georgetown")
    assert(series.size == 3)
  }

  test("Find population") {
    val fmt = new SimpleDateFormat("MM/dd/yyyy")

    val jan01: Date = fmt.parse("01/01/2011")
    val s = scheduleDao.save(new Schedule(0L, "test", "Test"))
    val c = conferenceDao.save(new Conference(0L, s, "big-east", "Big East"))
    val rr = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val rs = teamDao.save(new Team(key = "villanova", name = "Villanova", schedule = s, conference = c, longName = "Villanova", updatedAt = new Date))
    val rt = teamDao.save(new Team(key = "syracuse", name = "Syracuse", schedule = s, conference = c, longName = "Syracuse", updatedAt = new Date))

    val mst = metaStatDao.save(new MetaStat(key = "wins", name = "Wins", higherIsBetter = true))

    teamStatDao.save(new TeamStat(metaStat = mst, team = rr, date = jan01, value = 38.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = rs, date = jan01, value = 28.0))
    teamStatDao.save(new TeamStat(metaStat = mst, team = rt, date = jan01, value = 29.0))

    val pop: List[TeamStat] = teamStatDao.population("wins", jan01)
    assert(pop.size == 3)
  }
}