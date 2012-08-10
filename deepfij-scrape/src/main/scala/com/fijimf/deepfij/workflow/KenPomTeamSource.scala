package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{Schedule, TeamDao, Team}
import java.util.Date

class KenPomTeamSource(parameters: Map[String, String]) extends DataSource[Team] {
  val teamDao = new TeamDao

  def load = null

  def loadAsOf(date: Date) = null

  def build(schedule: Schedule, data: Map[String, String]) = null

  def update(t: Team, data: Map[String, String]) = null

  def verify(t: Team, u: Team) = false
}
