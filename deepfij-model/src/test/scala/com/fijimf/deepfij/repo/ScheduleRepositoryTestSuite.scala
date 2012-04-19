package com.fijimf.deepfij.repo

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import com.fijimf.deepfij.modelx._
import java.util.Date


@RunWith(classOf[JUnitRunner])
class ScheduleRepositoryTestSuite extends FunSuite with BeforeAndAfterEach {
  val sdao: ScheduleDao = new ScheduleDao
  val cdao: ConferenceDao = new ConferenceDao
  val dao: TeamDao = new TeamDao

  override def beforeEach() {
    PersistenceSource.schemaExport.execute(false, true, false, false)
    PersistenceSource.entityManager.clear()
  }

  val cm = Map("a" -> "A", "b" -> "B", "c" -> "C", "d" -> "D", "e" -> "E", "f" -> "F")
  val td = List(
    Map("key" -> "georgetown", "name" -> "Georgetown", "longName" -> "Georgetown", "conference" -> "b"),
    Map("key" -> "villanova", "name" -> "Villanova", "longName" -> "Villanova", "conference" -> "b"),
    Map("key" -> "syracuse", "name" -> "Syracuse", "longName" -> "Syracuse", "conference" -> "b")
  )
  val al = Map("Gtown" -> "georgetown")
  val gd = List((new Date, "Gtown", "Villanova"))
  val rd = List((new Date, "Gtown", 100, "Villanova", 99))

  test("ScheduleRepo simplest dropCreate") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    assert(sdao.findAll().size == 1)

    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.isEmpty)
    assert(schedule.teamList.isEmpty)
    assert(schedule.aliasList.isEmpty)
    assert(schedule.gameList.isEmpty)
  }

  test("ScheduleRepo multiple calls to dropCreate") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    assert(sdao.findAll().size == 1)
    repo.dropCreateSchedule("test", "Test")
    assert(sdao.findAll().size == 1)
    repo.dropCreateSchedule("test2", "Test2")
    assert(sdao.findAll().size == 2)
  }

  test("ScheduleRepo simplest dropCreate + conferences") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    repo.createConferences("test", cm)

    assert(sdao.findAll().size == 1)
    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.size == 6)
    assert(schedule.teamList.isEmpty)
    assert(schedule.aliasList.isEmpty)
    assert(schedule.gameList.isEmpty)

  }
  test("ScheduleRepo dropCreate + conferences + teams") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    repo.createConferences("test", cm)
    repo.createTeams("test", td)

    assert(sdao.findAll().size == 1)
    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.size == 6)
    assert(schedule.teamList.size == 3)
    assert(schedule.aliasList.isEmpty)
    assert(schedule.gameList.isEmpty)

  }

  test("ScheduleRepo dropCreate + conferences + teams + aliases") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    repo.createConferences("test", cm)
    repo.createTeams("test", td)
    repo.createTeamAliases("test", al)

    assert(sdao.findAll().size == 1)
    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.size == 6)
    assert(schedule.teamList.size == 3)
    assert(schedule.aliasList.size == 1)
    assert(schedule.gameList.isEmpty)
  }

  test("ScheduleRepo simplest dropCreate + conferences + teams + aliases + games") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    repo.createConferences("test", cm)
    repo.createTeams("test", td)
    repo.createTeamAliases("test", al)
    repo.createGames("test", gd)

    assert(sdao.findAll().size == 1)
    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.size == 6)
    assert(schedule.teamList.size == 3)
    assert(schedule.aliasList.size == 1)
    assert(schedule.gameList.size == 1)
  }

  test("ScheduleRepo simplest dropCreate + conferences + teams + aliases + games + results") {
    val repo = new ScheduleRepository
    repo.dropCreateSchedule("test", "Test")
    repo.createConferences("test", cm)
    repo.createTeams("test", td)
    repo.createTeamAliases("test", al)
    repo.createGames("test", gd)
    repo.updateResults("test", rd)

    assert(sdao.findAll().size == 1)
    val schedule = sdao.findByKey("test").get
    assert(schedule.conferenceList.size == 6)
    assert(schedule.teamList.size == 3)
    assert(schedule.aliasList.size == 1)
    assert(schedule.gameList.size == 1)
    val gg = schedule.gameList.head
    assert(gg.resultOpt.isDefined)
  }


}