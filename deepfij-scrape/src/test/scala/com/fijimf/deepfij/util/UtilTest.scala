package com.fijimf.deepfij.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import junit.framework.Assert._


@RunWith(classOf[JUnitRunner])
class UtilTest extends FunSuite {

  import Util._

  test("textKey") {

    assertEquals(textKey("St. John's"), "st-johns")
    assertEquals(textKey(" Holy Cross"), "holy-cross")
    assertEquals(textKey("William & Mary"), "william-mary")
    assertEquals(textKey("St. Francis (PA)"), "st-francis-pa")
    assertEquals(textKey("TX-Pan American"), "tx-pan-american")
  }


}