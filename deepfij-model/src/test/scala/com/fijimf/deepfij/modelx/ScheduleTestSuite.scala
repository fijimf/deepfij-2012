package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import javax.persistence.PersistenceException

@RunWith(classOf[JUnitRunner])
class ScheduleTestSuite extends FunSuite with BeforeAndAfterEach {
  val dao: ScheduleDao = new ScheduleDao

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
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

    assert(dao.findByKey("nnn").get.isPrimary==false)
    assert(dao.findByKey("ooo").get.isPrimary==false)
    assert(dao.findByKey("ppp").get.isPrimary==false)

    dao.setPrimary("ooo")
 //   PersistenceSource.entityManager.clear() //Flush 1st level cache

    assert(dao.findByKey("nnn").get.isPrimary==false)
    assert(dao.findByKey("ooo").get.isPrimary)
    assert(dao.findByKey("ppp").get.isPrimary==false)

    dao.setPrimary("ppp")
//    PersistenceSource.entityManager.clear() //Flush 1st level cache


    assert(dao.findByKey("nnn").get.isPrimary==false)
    assert(dao.findByKey("ooo").get.isPrimary==false)
    assert(dao.findByKey("ppp").get.isPrimary)


  }


  override protected def afterEach() {
    PersistenceSource.dropDatabase()
  }
}