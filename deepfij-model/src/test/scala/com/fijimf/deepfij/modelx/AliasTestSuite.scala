package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import java.util.Date


@RunWith(classOf[JUnitRunner])
class AliasTestSuite extends DaoTestSuite {

  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val aliasDao = new AliasDao

  test("Create an alias") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    val r = teamDao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))
    val a = aliasDao.save(new Alias(schedule = s, team = r, alias = "Gtown"))
    assert(a.id > 0)

    val schedule: Schedule = scheduleDao.findByKey("test").get
    assert(schedule.aliasList.size == 1)
  }
}