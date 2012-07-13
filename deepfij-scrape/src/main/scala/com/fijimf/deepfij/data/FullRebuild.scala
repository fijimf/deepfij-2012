package com.fijimf.deepfij.data

import com.fijimf.deepfij.repo.ScheduleRepository
import generic.{GameReader, TeamReader, ConferenceReader}
import java.util.Date

trait FullRebuild extends Workflow {
  self: ConferenceReader with TeamReader with AliasStrategy with GameReader =>

  def repository: ScheduleRepository


  def apply(key: String, name: String) {
    repository.dropCreateSchedule(key, name)
    repository.createConferences(key, conferenceMap)
    repository.createTeams(key, teamData)
    //repository.createTeamAliases(key, createAliasesValues(teamData, aliasList))
    gameList(new Date)

  }
}