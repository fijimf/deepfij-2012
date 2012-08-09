package com.fijimf.deepfij.util


object Validation {
  def validKey(k: String): Boolean = k.matches("[0-9a-z\\-]+")

  def validName(n: String): Boolean = n.matches("[0-9a-zA-Z\\-\\.\\'\\&\\,\\(\\) ]+")
}
