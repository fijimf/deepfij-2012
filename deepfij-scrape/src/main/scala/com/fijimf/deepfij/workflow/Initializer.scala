package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.KeyedObject

trait Initializer[T <: KeyedObject] extends Builder[T] {
  def load: List[Map[String, String]]
}