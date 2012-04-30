package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class MetaStatTestSuite extends FunSuite with BeforeAndAfterEach {
  val dao: MetaStatDao = new MetaStatDao

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
  }


  test("Create") {
    val mst = dao.save(new MetaStat(key = "wins", name = "Wins", higherIsBetter = true))
    assert(mst.id > 0)
    val d: Option[MetaStat] = dao.findBy(mst.id)
    assert(d.isDefined)
    assert(d.get.name == "Wins")
    assert(d.get.values.isEmpty)
  }
}