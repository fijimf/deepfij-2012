package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import javax.persistence.PersistenceException

@RunWith(classOf[JUnitRunner])
class ConferenceTestSuite extends DaoTestSuite {

  val scheduleDao = new ScheduleDao
  val conferenceDao = new ConferenceDao
  val teamDao = new TeamDao
  val gameDao = new GameDao

  test("Create a conference") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    assert(c.id > 0)
  }

  test("Find a conference") {
    val s = scheduleDao.save(new Schedule(0L, "test", "Test", true))
    val s1 = scheduleDao.save(new Schedule(0L, "not-primary", "Not Primary"))
    assert(conferenceDao.findAll().isEmpty)
    assert(None == conferenceDao.findBy(999))
    val r = conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s1))
    val cs: List[Conference] = conferenceDao.findAll()
    assert(cs.size == 2)
    assert(cs.head.key == "big-east")
    val r1 = conferenceDao.findBy(r.id).get
    //assert(r1.isDefined)
    assert(r1.id > 0)
    assert(r1.key == "big-east")
    assert(r1.name == "Big East")
    assert(r1.schedule.key == s.key)
    assert(r1.schedule.name == s.name)
    assert(r1.teamList.isEmpty)

    assert(r1 == conferenceDao.findByKey("big-east").get)
    assert(r1 == conferenceDao.findByKey("test", "big-east").get)
    assert(r1 != conferenceDao.findByKey("not-primary", "big-east").get)
  }

  test("Schedule knows conferences") {
    val s = scheduleDao.save(new Schedule(0L, "test", "Test"))
    assert(conferenceDao.findAll().isEmpty)
    assert(None == conferenceDao.findBy(999))
    conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val s1 = scheduleDao.findBy(s.id).get
    assert(s1.conferenceList.size == 1)
  }


  test("Conference name is unique") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    conferenceDao.save(new Conference(schedule = s, key = "big-east", name = "Big East"))
    val ex = intercept[RuntimeException] {
      conferenceDao.save(new Conference(schedule = s, key = "big-eastx", name = "Big East"))
      fail("Expected exception not thrown")
    }
    assert(ex.getCause.isInstanceOf[PersistenceException])
  }

  test("Conference key is unique") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))

    conferenceDao.save(new Conference(schedule = s, key = "big-east", name = "Big East"))
    val ex = intercept[RuntimeException] {
      conferenceDao.save(new Conference(schedule = s, key = "big-east", name = "BigEast"))
      fail("Expected exception not thrown")
    }
    assert(ex.getCause.isInstanceOf[PersistenceException])

  }

  test("Conference team relationship") {
    val s = scheduleDao.save(new Schedule(key = "test", name = "Test"))
    val c = conferenceDao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val t = teamDao.save(new Team(key = "georgetown", name = "Georgetown", longName = "Georgetown", conference = c, schedule = s))
    assert(t.id > 0)

    val t1 = teamDao.findAll().head
    val c1 = conferenceDao.findAll().head
    assert(t1.conference == c1)
    assert(!c1.teamList.isEmpty)
    assert(c1.teamList.contains(t1))
  }
}