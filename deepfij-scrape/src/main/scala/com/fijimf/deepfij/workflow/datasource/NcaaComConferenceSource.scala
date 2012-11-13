package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.workflow.{Updater, Verifier, Initializer}

class NcaaComConferenceSource extends Initializer[Conference] with Updater[Conference] with Verifier[Conference] with ConferenceBuilder {

  def load: List[Map[String, String]] = {
    val toSet = NcaaTeamScraper.teamData.flatMap(_.get("conference")).toSet
    toSet.map(n => (Map[String, String]("key" -> nameToKey(n), "name" -> n))).toList
  }

  def loadAsOf(date: Date) = load

  def verify(t: Conference, u: Conference) = t.key == u.key && t.name == u.name
}


