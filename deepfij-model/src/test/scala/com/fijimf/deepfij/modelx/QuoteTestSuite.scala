package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class QuoteTestSuite extends DaoTestSuite{
  val dao: QuoteDao = new QuoteDao

  test("Find") {
    dao.findAll()
    dao.findBy(999)
    val q = new Quote(quote = "Is it the hat?")
    val r = dao.save(q)
    dao.findAll()
    print(q)
    print(r)

  }

  test("Find Random") {
    val r = dao.save(new Quote(quote = "Is it the hat?"))
    val q: Option[Quote] = dao.random()
    print(q)
    assert(r.quote == q.get.quote)
  }
}