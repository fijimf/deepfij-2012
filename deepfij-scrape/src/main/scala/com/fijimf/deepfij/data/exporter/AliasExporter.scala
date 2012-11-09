package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.workflow.datasource.{AliasBuilder, Exporter}
import com.fijimf.deepfij.modelx.Alias
import com.fijimf.deepfij.util.Logging
import java.io.FileInputStream

class AliasExporter (parms: Map[String, String]) extends Exporter[Alias] with AliasBuilder with Logging {

    lazy val inputStream = new FileInputStream(parms("fileName"))

    def fromString(s: String): Map[String, String] = {
      s.split('|').toList match {
        case alias :: team :: Nil => Map("alias"->alias,"team"->team)
        case _ => Map.empty[String, String]
      }
    }

    def toString(c: Alias): String = {
      c.key + "|" + c.team.key
    }

  def update(t: Alias, data: Map[String, String]) = null

  def verify(t: Alias, u: Alias) = false
}