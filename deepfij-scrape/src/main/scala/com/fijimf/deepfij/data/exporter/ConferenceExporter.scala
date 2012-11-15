package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.workflow.Exporter
import com.fijimf.deepfij.workflow.datasource.ConferenceBuilder
import com.fijimf.deepfij.modelx.Conference
import com.fijimf.deepfij.util.Logging

class ConferenceExporter(parms: Map[String, String]) extends Exporter[Conference] with ConferenceBuilder with Logging {
  def fileName = parms("fileName")


  def fromString(s: String): Map[String, String] = {
    s.split('|').toList match {
      case key :: name :: Nil => Map("key" -> key, "name" -> name)
      case _ => Map.empty[String, String]
    }
  }

  def toString(c: Conference): String = {
    c.key + "|" + c.name
  }
}
