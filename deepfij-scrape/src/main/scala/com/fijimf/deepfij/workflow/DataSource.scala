package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.{Schedule, KeyedObject}
import java.util.Date

trait DataSource[T <: KeyedObject] {

  def schedule: Schedule

  def load: List[Map[String, String]]

  def loadAsOf(date: Date): List[T]

  def fromKey(key: String): Option[T]

  def build(data: Map[String, String]): Option[T]

  def update(t: T, data: Map[String, String]): T

  def verify(t: T, data: Map[String, String]): Option[(T, T)]
}
