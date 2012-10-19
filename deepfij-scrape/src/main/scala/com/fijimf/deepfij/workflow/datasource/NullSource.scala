package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx._
import java.util.Date

class NullSource[T <: KeyedObject] extends DataSource[T] {
  def load = List.empty[Map[String, String]]

  def loadAsOf(date: Date) = List.empty[Map[String, String]]

  def build(schedule: Schedule, data: Map[String, String]) = None

  def update(t: T, data: Map[String, String]) = t

  def verify(t: T, u: T) = false
}

class NullConferenceSource extends NullSource[Conference]

class NullAliasSource extends NullSource[Alias]

class NullTeamSource extends NullSource[Team]

class NullGameSource extends NullSource[Game]

class NullResultSource extends NullSource[Result]
