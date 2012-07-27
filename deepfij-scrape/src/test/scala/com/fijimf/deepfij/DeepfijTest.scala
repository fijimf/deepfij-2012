package com.fijimf.deepfij

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

@RunWith(classOf[JUnitRunner])
class DeepfijTest extends FunSpec {
  describe("The Deepfij thingee ") {
    val df = Deepfij("deepfij-base.xml")
    it("can be read from an included xml file") {
      df should not be (null)
    }

  }

}
