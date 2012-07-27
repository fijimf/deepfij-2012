package com.fijimf.deepfij

import modelx._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSpec, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._


@RunWith(classOf[JUnitRunner])
class DeepfijTest extends FunSpec with BeforeAndAfterEach {

  System.setProperty("deepfij.persistenceUnitName", "deepfij-test")

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()

  }

  describe("A Deeepfij object from the deepfij-base.xml ") {
    val df = Deepfij("deepfij-base.xml")
    it("can be read from an included xml file") {
      df should not be (null)
    }
    it("should have one factory") {
      df.fs.size should be(1)
    }


  }

}
