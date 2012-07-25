package com.fijimf.deepfij.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite


@RunWith(classOf[JUnitRunner])
class UtilTest extends FunSuite {

  import Util._

  test("textKey") {

    assert(textKey("St. John's") == "st-johns")
    assert(textKey(" Holy Cross") == "holy-cross")
    assert(textKey("William & Mary") == "william-mary")
    assert(textKey("St. Francis (PA)") == "st-francis-pa")
    assert(textKey("TX-Pan American") == "tx-pan-american")
  }


}