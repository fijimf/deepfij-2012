package com.fijimf.deepfij.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.util.Date

@RunWith(classOf[JUnitRunner])
class DateStreamTest extends FunSuite {


  test("From & To == Now") {
    val stream: DateStream = DateStream(new Date(), new Date())
    assert(stream.size == 1)
  }


}