package com.fijimf.deepfij.util

trait FromStringMap[T] {
  def requiredFields: Set[String]

  def apply(fields: Map[String, String]): T
}
