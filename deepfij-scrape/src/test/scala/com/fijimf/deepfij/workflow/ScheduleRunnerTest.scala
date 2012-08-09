package com.fijimf.deepfij.workflow

import org.scalatest.{BeforeAndAfterEach, FunSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.fijimf.deepfij.modelx.{Schedule, ScheduleDao, PersistenceSource}


@RunWith(classOf[JUnitRunner])
class ScheduleRunnerTest extends FunSpec with BeforeAndAfterEach {

  System.setProperty("deepfij.persistenceUnitName", "deepfij-test")

  val scheduleDao = new ScheduleDao()

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
    scheduleDao.save(new Schedule(name = "NCAA 2011-2012", key = "ncaa2012"))


  }

  describe("A schedule runner ") {

    it("must have valid name and key") {
      intercept[IllegalArgumentException](
        ScheduleRunner(
          "",
          "Name",
          NotInitialized,
          List(new NullConferenceSource),
          List(new NullTeamSource),
          List(new NullAliasSource),
          List(new NullGameSource),
          List(new NullResultSource)
        )
      )
      intercept[IllegalArgumentException](
        ScheduleRunner(
          "key",
          "",
          NotInitialized,
          List(new NullConferenceSource),
          List(new NullTeamSource),
          List(new NullAliasSource),
          List(new NullGameSource),
          List(new NullResultSource)
        )
      )
    }

    it("must have nonempty lists of Sources") {
      intercept[IllegalArgumentException](
        ScheduleRunner(
          "key",
          "Name",
          NotInitialized,
          List.empty,
          List(new NullTeamSource),
          List(new NullAliasSource),
          List(new NullGameSource),
          List(new NullResultSource)
        )
      )
    }
  }

}
