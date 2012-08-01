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

  val baseXml = """
                  |<deepfij>
                  |    <schedule name="NCAA 2011-2012" key="ncaa2012">
                  |        <conferences>
                  |            <reader class="com.fijimf.deepfij.workflow.ConferenceSource"/>
                  |        </conferences>
                  |        <aliases>
                  |            <reader class="com.fijimf.deepfij.workflow.NullAliasSource"/>
                  |        </aliases>
                  |        <teams>
                  |            <reader class="com.fijimf.deepfij.workflow.NullTeamSource"/>
                  |        </teams>
                  |        <games>
                  |            <reader class="com.fijimf.deepfij.workflow.NullGameSource"/>
                  |        </games>
                  |        <results>
                  |            <reader class="com.fijimf.deepfij.workflow.NullResultSource"/>
                  |        </results>
                  |    </schedule>
                  |</deepfij>
                """.stripMargin
  describe("A Deeepfij object from xml ") {
    val df = Deepfij(baseXml)
    it("can be read ") {
      df should not be (null)
      df.managers.size should be(1)
      val mgr = df.managers.head
      mgr.name should be("NCAA 2011-2012")
      mgr.key should be("ncaa2012")
    }
  }

}
