package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.KeyedObject
import java.util.Date

trait Updater[T <: KeyedObject] extends Builder[T] with Verifier[T] {
  def loadAsOf(date: Date): List[Map[String, String]]

}