package com.fijimf.deepfij.data.ncaa

import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}
import com.fijimf.deepfij.workflow.datasource.TeamBuilder

class NcaaComTeamSource() extends Initializer[Team] with Updater[Team] with Verifier[Team] with TeamBuilder {

  def load: List[Map[String, String]] = {
    NcaaTeamScraper.teamData
  }

  def update(date: Date): List[Map[String, String]] = {
    List.empty
  }


  def update(c: Team, data: Map[String, String]): Team = {
    for (n <- data.get("name")) yield {
      c.name = n
    }
    c
  }

  def loadAsOf(date: Date) = load

  def isSame(t: Team, u: Team) =
    t.officialUrl == u.officialUrl && t.logo == u.logo &&
      t.conference == u.conference && t.nickname == u.nickname &&
      t.primaryColor == u.primaryColor && t.secondaryColor == u.secondaryColor &&
      t.longName == u.longName
}


