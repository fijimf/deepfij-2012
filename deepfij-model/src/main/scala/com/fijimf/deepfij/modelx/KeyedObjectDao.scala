package com.fijimf.deepfij.modelx


trait KeyedObjectDao[T <: KeyedObject] {
  def findByKey(k: String): Option[T]
}
