package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class TeamTestSuite extends FunSuite with BeforeAndAfterEach {
  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val dao: TeamDao = new TeamDao


  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
  }


  test("Find") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    assert(dao.findAll().isEmpty)
    assert(dao.findBy(999) == None)
    val r = dao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    assert(dao.findAll().size == 1)
    val r1 = dao.findAll().head
    assert(r1 != null)
    assert(r1.homeGameList.isEmpty)
    assert(r1.awayGameList.isEmpty)
    assert(r1.games.isEmpty)
    assert(r1.schedule.conferences.size() == 1)
    assert(r1.schedule.teams.size() == 1)
    assert(r1.schedule.games.isEmpty)

  }

  test("Find by key") {
    val s = sdao.save(new Schedule(0L, "test", "Test"))
    val c = cdao.save(new Conference(0L, s, "big-east", "Big East"))
    assert(dao.findAll().isEmpty)
    assert(dao.findBy(999) == None)
    val r = dao.save(new Team(key = "georgetown", name = "Georgetown", schedule = s, conference = c, longName = "Georgetown", updatedAt = new Date))

    assert(dao.findByKey("georgetown").size == 1)
    assert(dao.findByKey("test","georgetown").size == 1)


  }

  test("Team constraints - blank key") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    intercept[IllegalArgumentException] {
      new Team(key = "", name = "Georgetown", longName = "Georgetown", conference = c, schedule = s)
      fail("Expected exception not thrown")
    }
  }
  test("Team constraints - blank name") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    intercept[IllegalArgumentException] {
      new Team(key = "georgetown", name = "", longName = "Georgetown", conference = c, schedule = s)
      fail("Expected exception not thrown")
    }
  }
  test("Team constraints - bad key") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    intercept[IllegalArgumentException] {
      new Team(key = "Texas A & M", name = "Texas A & M", longName = "Texas A & M", conference = c, schedule = s)
      fail("Expected exception not thrown")
    }
  }
  test("Team constraints - bad name") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    intercept[IllegalArgumentException] {
      new Team(key = "texas-a-m", name = "Texas A + M", longName = "Texas A + M", conference = c, schedule = s)
      fail("Expected exception not thrown")
    }
  }
  test("Team constraints - OK") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(schedule = s, key = "big-12", name = "Big XII"))
    new Team(key = "", name = "", longName = "", conference = c, schedule = s) //OK
    new Team(key = "texas-a-m", name = "Texas A & M", longName = "Texas A & M", conference = c, schedule = s) //OK
  }

  test("Create a team") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val t = dao.save(new Team(key = "georgetown", name = "Georgetown", longName = "Georgetown", conference = c, schedule = s))
    assert(t.id > 0)
    val s1 = sdao.findByKey("test")
    assert(s1.isDefined)
    assert(s1.get.conferenceList.size == 1)
    assert(s1.get.teamList.size == 1)
  }

  test("Team key is unique") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val t1 = dao.save(new Team(key = "villanova", name = "Villanova", longName = "Villanova", conference = c, schedule = s))
    assert(t1.id > 0)
    val ex = intercept[Exception] {
      val t2 = dao.save(new Team(key = "villanova", name = "X-Villanova", longName = "X-Villanova", conference = c, schedule = s))
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[RuntimeException])

  }
  test("Team name is unique") {
    val s = sdao.save(new Schedule(key = "test", name = "Test"))
    val c = cdao.save(new Conference(key = "big-east", name = "Big East", schedule = s))
    val t1 = dao.save(new Team(key = "syracuse", name = "Syracuse", longName = "Syracuse", conference = c, schedule = s))
    assert(t1.id > 0)
    val ex = intercept[Exception] {
      val t2 = dao.save(new Team(key = "syracusex", name = "Syracuse", longName = "Syracuse", conference = c, schedule = s))
      fail("Expected exception not thrown")
    }
    assert(ex.isInstanceOf[RuntimeException])
  }


  override protected def afterEach() {
    PersistenceSource.dropDatabase()
  }
}