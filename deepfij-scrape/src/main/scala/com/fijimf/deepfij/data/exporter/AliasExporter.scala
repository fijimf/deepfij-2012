package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.workflow.Exporter
import com.fijimf.deepfij.workflow.datasource.AliasBuilder
import com.fijimf.deepfij.modelx.Alias
import com.fijimf.deepfij.util.Logging

class AliasExporter(parms: Map[String, String]) extends Exporter[Alias] with AliasBuilder with Logging {
  def fileName = parms("fileName")

  def dataDir = parms("dataDir")

  def fromString(s: String): Map[String, String] = {
    s.split('|').toList match {
      case alias :: team :: Nil => Map("alias" -> alias, "team" -> team)
      case _ => Map.empty[String, String]
    }
  }

  def toString(c: Alias): String = {
    c.key + "|" + c.team.key
  }
}