package com.fijimf.deepfij.workflow.datasource

import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class NcaaComTeamSource() extends DataSource[Team] with TeamBuilder {

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

  def verify(t: Team, u: Team) = t.key == u.key && t.name == u.name
}


