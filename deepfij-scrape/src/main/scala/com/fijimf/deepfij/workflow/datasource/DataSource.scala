package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.modelx.{Schedule, KeyedObject}
import java.util.Date

trait DataSource[T <: KeyedObject] extends Builder[T] {

  def load: List[Map[String, String]]

  def loadAsOf(date: Date): List[Map[String, String]]

  def build(schedule: Schedule, data: Map[String, String]): Option[T]

  def update(t: T, data: Map[String, String]): T

  def verify(t: T, u: T): Boolean
}
