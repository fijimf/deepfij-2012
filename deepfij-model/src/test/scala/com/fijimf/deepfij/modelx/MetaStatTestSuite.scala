package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MetaStatTestSuite extends DaoTestSuite {
  val dao: MetaStatDao = new MetaStatDao

  test("Create") {
    val mst = dao.save(new MetaStat(statKey = "wins", name = "Wins", higherIsBetter = true))
    assert(mst.id > 0)
    val d: Option[MetaStat] = dao.findBy(mst.id)
    assert(d.isDefined)
    assert(d.get.name == "Wins")
    assert(d.get.values.isEmpty)
  }

  test("Find by key") {
    val mst = dao.save(new MetaStat(statKey = "wins", name = "Wins", higherIsBetter = true))
    assert(mst.id > 0)
    val d: Option[MetaStat] = dao.findByStatKey("wins")
    assert(d.isDefined)
    assert(d.get.name == "Wins")
    assert(d.get.values.isEmpty)
  }

}