package com.fijimf.deepfij.modelx

trait FindByKey[T] {
  def findByKey(k: String): Option[T]
}
