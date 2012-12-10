package com.fijimf.deepfij.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import junit.framework.Assert._


@RunWith(classOf[JUnitRunner])
class UtilTest extends FunSuite {


  test("textKey") {

    assertEquals(Util.textKey("St. John's"), "st-johns")
    assertEquals(Util.textKey(" Holy Cross"), "holy-cross")
    assertEquals(Util.textKey("William & Mary"), "william-mary")
    assertEquals(Util.textKey("St. Francis (PA)"), "st-francis-pa")
    assertEquals(Util.textKey("TX-Pan American"), "tx-pan-american")
  }


}