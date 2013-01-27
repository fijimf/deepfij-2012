package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QuoteTestSuite extends DaoTestSuite {
  val dao: QuoteDao = new QuoteDao

  test("Find") {
    assert(dao.findAll().isEmpty)
    assert(dao.findBy(999).isEmpty)
    val q = new Quote(quote = "Is it the hat?")
    val r = dao.save(q)
    assert(dao.findAll().size == 1)
  }

  test("Find Random") {
    dao.save(new Quote(quote = "Is it the hat?"))
    dao.save(new Quote(quote = "Is it my hat?"))
    dao.save(new Quote(quote = "Is it his hat?"))
    val q: Option[Quote] = dao.random()
    assert(q.isDefined)
  }
}