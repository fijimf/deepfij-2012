package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.workflow.datasource.{ConferenceBuilder, Exporter}
import com.fijimf.deepfij.modelx.Conference
import com.fijimf.deepfij.util.Logging
import java.io.FileInputStream

class ConferenceExporter (parms: Map[String, String]) extends Exporter[Conference] with ConferenceBuilder with Logging {

    lazy val inputStream = new FileInputStream(parms("fileName"))

    def fromString(s: String): Map[String, String] = {
      s.split('|').toList match {
        case key :: name :: Nil => Map("key"->key,"name"->name)
        case _ => Map.empty[String, String]
      }
    }

    def toString(c: Conference): String = {
      c.key + "|" + c.name
    }

  def update(t: Conference, data: Map[String, String]) = null

  def verify(t: Conference, u: Conference) = false
}
