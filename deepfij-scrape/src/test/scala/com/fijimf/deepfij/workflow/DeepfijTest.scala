package com.fijimf.deepfij.workflow

import org.scalatest.{BeforeAndAfterEach, FunSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import com.fijimf.deepfij.modelx.{Schedule, ScheduleDao, PersistenceSource}


@RunWith(classOf[JUnitRunner])
class DeepfijTest extends FunSpec with BeforeAndAfterEach {

  System.setProperty("deepfij.persistenceUnitName", "deepfij-test")

  val scheduleDao = new ScheduleDao()

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
    scheduleDao.save(new Schedule(name = "NCAA 2011-2012", key = "ncaa2012"))


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

    it("can support cold startup use case") {
      val df = Deepfij(baseXml)
      df.managers.map(_.coldStartup)
    }

    it("can support warm startup use case") {
      val df = Deepfij(baseXml)
      df.managers.map(_.warmStartup)
    }

    it("will drop schedule managers on an exception in startup") {
      val df = Deepfij(baseXml)
      val df1 = df.copy(managers = df.managers.map(_.hotStartup))
      df1.managers.size should be(1)
    }

    it("can parse Readers with no initial parameters") {
      val node =
        <schedule name="NCAA 2011-2012" key="ncaa2012">
          <conferences>
            <reader class="com.fijimf.deepfij.workflow.NullConferenceSource"/>
          </conferences>
        </schedule>

      Deepfij.parseReaders(node, "conferences").size should be(1)

    }

    it("can parse Readers with Map[String,String] parameters") {
      val node =
        <schedule name="NCAA 2011-2012" key="ncaa2012">
          <teams>
            <reader class="com.fijimf.deepfij.workflow.KenPomTeamSource">
              <parameter key="location" value="http://www.kenpom.com/ncaab2012"/>
            </reader>
          </teams>
        </schedule>

      Deepfij.parseReaders(node, "teams").size should be(1)

    }

    it("can parse multiple readers") {
      val node =
        <schedule name="NCAA 2011-2012" key="ncaa2012">
          <conferences>
            <reader class="com.fijimf.deepfij.workflow.NcaaComConferenceSource"/>
            <reader class="com.fijimf.deepfij.workflow.NullConferenceSource"/>
          </conferences>
          <teams>
            <reader class="com.fijimf.deepfij.workflow.KenPomTeamSource">
              <parameter key="location" value="http://www.kenpom.com/ncaab2012"/>
            </reader>
            <reader class="com.fijimf.deepfij.workflow.KenPomTeamSource">
              <parameter key="location" value="http://www.kenpom.com/ncaab2011"/>
            </reader>
            <reader class="com.fijimf.deepfij.workflow.NullTeamSource"/>
          </teams>
        </schedule>
      Deepfij.parseReaders(node, "teams").size should be(3)
      Deepfij.parseReaders(node, "conferences").size should be(2)

    }
  }

}
