package com.fijimf.deepfij.workflow

import datasource._
import org.scalatest.{BeforeAndAfterEach, FunSpec}
import org.scalatest.matchers.ShouldMatchers._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.fijimf.deepfij.modelx._


@RunWith(classOf[JUnitRunner])
class ScheduleRunnerTest extends FunSpec with BeforeAndAfterEach {

  System.setProperty("deepfij.persistenceUnitName", "deepfij-test")

  val scheduleDao = new ScheduleDao()
  val teamDao = new TeamDao()
  val conferenceDao = new ConferenceDao()
  val baseRunner = ScheduleRunner(
    "key",
    "Name",
    NotInitialized,
    List(new NullConferenceSource),
    List(new NullTeamSource),
    List(new NullAliasSource),
    List(new NullGameSource),
    List(new NullResultSource)
  )

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
    val s = scheduleDao.save(new Schedule(name = "NCAA 2011-2012", key = "ncaa2012"))
    val c = conferenceDao.save(new Conference(schedule = s, key = "test", name = "Test"))
    val t = teamDao.save(new Team(schedule = s, conference = c, key = "test", name = "Test", longName = "Test"))
  }

  describe("A schedule runner ") {

    it("must have valid name and key") {
      intercept[IllegalArgumentException](baseRunner.copy(name = ""))
      intercept[IllegalArgumentException](baseRunner.copy(key = ""))
      intercept[IllegalArgumentException](baseRunner.copy(name = null))
      intercept[IllegalArgumentException](baseRunner.copy(key = null))
      intercept[IllegalArgumentException](baseRunner.copy(key = "%^<<"))
      intercept[IllegalArgumentException](baseRunner.copy(key = "NO-CAPS"))
    }

    it("must have nonempty lists of Sources") {
      intercept[IllegalArgumentException](baseRunner.copy(conferenceReaders = List.empty))
      intercept[IllegalArgumentException](baseRunner.copy(teamReaders = List.empty))
      intercept[IllegalArgumentException](baseRunner.copy(aliasReaders = List.empty))
      intercept[IllegalArgumentException](baseRunner.copy(gameReaders = List.empty))
      intercept[IllegalArgumentException](baseRunner.copy(resultReaders = List.empty))
    }

    it("should have a state of Running after a successful startup") {
      baseRunner.status should be(NotInitialized)
      baseRunner.coldStartup.status should be(Running)

      baseRunner.status should be(NotInitialized)
      baseRunner.warmStartup.status should be(Running)

      baseRunner.status should be(NotInitialized)
      intercept[IllegalArgumentException](baseRunner.hotStartup)

    }

    it("should throw an exception if startup is called after initialization") {
      intercept[IllegalStateException](baseRunner.coldStartup.coldStartup)
      intercept[IllegalStateException](baseRunner.coldStartup.warmStartup)
      intercept[IllegalStateException](baseRunner.coldStartup.hotStartup)
      intercept[IllegalStateException](baseRunner.warmStartup.coldStartup)
      intercept[IllegalStateException](baseRunner.warmStartup.warmStartup)
      intercept[IllegalStateException](baseRunner.warmStartup.hotStartup)
    }
  }

}
