package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import javax.persistence.PersistenceException
import org.apache.commons.lang.StringUtils

@RunWith(classOf[JUnitRunner])
class ScheduleTestSuite extends DaoTestSuite {
  val dao: ScheduleDao = new ScheduleDao

  test("Empty constructor") {
    assert(StringUtils.isBlank(new Schedule().key))
    assert(StringUtils.isBlank(new Schedule().name))
  }

  test("Find empty") {
    assert(dao.findAll().isEmpty)
    assert(dao.findBy(999) == None)
    assert(dao.findByKey("Blah") == None)
  }

  test("Find after save") {
    val r = dao.save(new Schedule(key = "ncaa12", name = "NCAA Mens Div I Basketball 12"))
    assert(dao.findAll().size == 1)
    assert(dao.findBy(r.id).isDefined)
    assert(dao.findByKey("ncaa12").isDefined)
  }

  test("Delete") {
    val r = dao.save(new Schedule(key = "ncaa12", name = "NCAA Mens Div I Basketball 12"))
    val all: List[Schedule] = dao.findAll()
    assert(all.size == 1)
    assert(dao.findBy(r.id).isDefined)
    assert(dao.findByKey("ncaa12").isDefined)

    dao.delete(r.id)
    assert(dao.findAll().isEmpty)
    assert(dao.findBy(r.id) == None)
    assert(dao.findByKey("ncaa12") == None)
  }

  test("Schedule constraints - blank key") {
    intercept[IllegalArgumentException] {
      new Schedule(key = "", name = "NCAA Men's Basketball")
      fail("Expected exception not thrown")
    }
  }
  test("Schedule constraints - blank name") {
    intercept[IllegalArgumentException] {
      new Schedule(key = "ncaam11", name = "")
      fail("Expected exception not thrown")
    }
  }
  test("Schedule constraints - key bad char") {
    intercept[IllegalArgumentException] {
      new Schedule(key = "ncaa-men's", name = "NCAA Men's BasketballM")
      fail("Expected exception not thrown")
    }
  }
  test("Schedule constraints - name bad char") {
    intercept[IllegalArgumentException] {
      new Schedule(key = "ncaa-men's", name = "Reg Season + Championship")
      fail("Expected exception not thrown")
    }
  }

  test("Schedule constraints - OK") {
    new Schedule() //OK
    new Schedule(key = "ncaam01-02", name = "NCAA Men's 2001-2002") //OK
  }

  test("Dup name should fail") {
    dao.save(new Schedule(key = "ncaa12", name = "NCAA Mens Div I Basketball 12"))
    val ex = intercept[RuntimeException] {
      dao.save(new Schedule(key = "ncaa12a", name = "NCAA Mens Div I Basketball 12"))
      fail("Expected exception not thrown")
    }
    assert(ex.getCause.isInstanceOf[PersistenceException])


  }

  test("Dup key should fail") {
    dao.save(new Schedule(key = "ncaa12", name = "NCAA Mens Div I Basketball 12"))
    val ex = intercept[RuntimeException] {
      dao.save(new Schedule(key = "ncaa12", name = "XXNCAA Mens Div I Basketball 12"))
      fail("Expected exception not thrown")
    }
    assert(ex.getCause.isInstanceOf[PersistenceException])

  }

  test("Set primary") {
    dao.save(new Schedule(key = "nnn", name = "NNN"))
    dao.save(new Schedule(key = "ooo", name = "OOO"))
    dao.save(new Schedule(key = "ppp", name = "PPP"))

    assert(dao.findByKey("nnn").get.isPrimary == false)
    assert(dao.findByKey("ooo").get.isPrimary == false)
    assert(dao.findByKey("ppp").get.isPrimary == false)

    dao.setPrimary("ooo")
    //   PersistenceSource.entityManager.clear() //Flush 1st level cache

    assert(dao.findByKey("nnn").get.isPrimary == false)
    assert(dao.findByKey("ooo").get.isPrimary)
    assert(dao.findByKey("ppp").get.isPrimary == false)

    dao.setPrimary("ppp")
    //    PersistenceSource.entityManager.clear() //Flush 1st level cache


    assert(dao.findByKey("nnn").get.isPrimary == false)
    assert(dao.findByKey("ooo").get.isPrimary == false)
    assert(dao.findByKey("ppp").get.isPrimary)

    assert(dao.findPrimary() == dao.findByKey("ppp"))


  }

  test("Data methods on new schedule") {
    val schedule: Schedule = new Schedule(key = "nnn", name = "NNN")
    assert(schedule.gameByKey == Map.empty[String, Game])
    assert(schedule.teamByKey == Map.empty[String, Team])
    assert(schedule.gameList == List.empty[Game])
    assert(schedule.teamList == List.empty[Team])
    assert(schedule.conferenceByKey == Map.empty[String, Conference])
    assert(schedule.conferenceByName == Map.empty[String, Conference])
    assert(schedule.conferenceList == List.empty[Conference])
    assert(schedule.aliasByKey == Map.empty[String, Alias])
    assert(schedule.aliasList == List.empty[Alias])
  }

  test("Data methods on retrieved empty schedule") {
    dao.save(new Schedule(key = "nnn", name = "NNN"))
    val schedule: Schedule = dao.findByKey("nnn").get
    assert(schedule.gameByKey == Map.empty[String, Game])
    assert(schedule.teamByKey == Map.empty[String, Team])
    assert(schedule.gameList == List.empty[Game])
    assert(schedule.teamList == List.empty[Team])
    assert(schedule.conferenceByKey == Map.empty[String, Conference])
    assert(schedule.conferenceByName == Map.empty[String, Conference])
    assert(schedule.conferenceList == List.empty[Conference])
    assert(schedule.aliasByKey == Map.empty[String, Alias])
    assert(schedule.aliasList == List.empty[Alias])
  }


}