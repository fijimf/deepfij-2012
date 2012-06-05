package com.fijimf.deepfij


import java.lang.String
import repo.ScheduleRepository


object NcaaComScraper {

  def main(args: Array[String]) {
    val repo = new ScheduleRepository
//    repo.dropCreateSchedule("ncaa12", "NCAA 20011-12 Men's Basketball")
//    repo.createConferences("ncaa12", NcaaTeamScraper.conferenceMap)
//    repo.createTeams("ncaa12", NcaaTeamScraper.teamData)

    val s = repo.scheduleDao.findByKey("ncaa12").get
     s.teamList.foreach(t=>println(t.name))

  }
}