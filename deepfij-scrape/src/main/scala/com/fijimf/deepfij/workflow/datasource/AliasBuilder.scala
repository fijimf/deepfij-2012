package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Alias, Schedule}
import com.fijimf.deepfij.util.Logging

trait AliasBuilder {
  self: Logging =>
  def build(schedule: Schedule, data: Map[String, String]) = {
    val key = data("team")
    val alias = data("alias")
    require(Option(key).isDefined && Option(alias).isDefined)
    val teamOption = schedule.teamByKey.get(key)
    if (teamOption.isDefined) {
      if (teamOption.get.name == alias) {
        log.info("Skipping " + alias + " -> " + alias + " because the alias is the same as the team name")
        None
      } else {
        Some(new Alias(schedule = schedule, team = teamOption.get, alias = alias))
      }
    } else {
      log.info("No team found for key " + key)
      None
    }
  }
}
