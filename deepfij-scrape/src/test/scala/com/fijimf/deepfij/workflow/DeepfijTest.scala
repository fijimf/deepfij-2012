package com.fijimf.deepfij.workflow

import org.scalatest.{BeforeAndAfterEach, FunSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import com.fijimf.deepfij.modelx.PersistenceSource


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
                  |            <reader class="com.fijimf.deepfij.workflow.NullConferenceSource"/>
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
                  |    <schedule name="NCAA 2012-2013" key="ncaa2013">
                  |        <conferences>
                  |            <reader class="com.fijimf.deepfij.workflow.NullConferenceSource"/>
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
  describe("A Deeepfij object ") {

    it("can be read and intialized from XML") {
      val df = Deepfij(baseXml)
      df should not be (null)
    }
    it("can support multiple schedules") {
      val df = Deepfij(baseXml)
      df.managers.size should be(2)

      val s2012 = df.managers(0)
      s2012.name should be("NCAA 2011-2012")
      s2012.key should be("ncaa2012")

      val s2013 = df.managers(1)
      s2013.name should be("NCAA 2012-2013")
      s2013.key should be("ncaa2013")
    }
  }

}
