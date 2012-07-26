package com.fijimf.deepfij

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class DeepfijTest extends FunSuite {
  test("readConfigFile") {
    val df = Deepfij("deepfij-base.xml")

  }

}
