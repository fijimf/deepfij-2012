package com.fijimf.deepfij

import modelx._
import org.scalatest.{BeforeAndAfterEach, FunSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import workflow.Deepfij


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


  }

}
