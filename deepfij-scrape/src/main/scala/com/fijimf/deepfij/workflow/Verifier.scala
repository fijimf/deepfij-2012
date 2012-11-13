package com.fijimf.deepfij.workflow

import com.fijimf.deepfij.modelx.KeyedObject

trait Verifier[T <: KeyedObject] extends Builder[T] {
  def verify(t: T, u: T): Boolean
}
