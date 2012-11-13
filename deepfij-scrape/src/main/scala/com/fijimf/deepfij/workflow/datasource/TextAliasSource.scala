package com.fijimf.deepfij.workflow.datasource

import java.io.InputStream
import io.{Source, BufferedSource}
import com.fijimf.deepfij.modelx.Alias
import java.util.Date
import com.fijimf.deepfij.util.Logging
import com.fijimf.deepfij.workflow.{Verifier, Updater, Initializer}

class TextAliasSource(parms: Map[String, String]) extends Initializer[Alias] with Updater[Alias] with Verifier[Alias] with AliasBuilder with Logging {

  lazy val aliasList: List[(String, String)] = {
    val resource: String = parms("resource")
    log.info("Loading aliases from " + resource)
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(resource)
    val src: BufferedSource = Source.fromInputStream(is)
    val lst: List[(String, String)] = src.getLines().map(_.split(",")).map(arr => (arr(0), arr(1))).toList
    log.info(lst.size + " potential aliases loaded.")
    lst
  }

  def load = aliasList.map(tup => Map("team" -> tup._2, "alias" -> tup._1))

  def loadAsOf(date: Date) = aliasList.map(tup => Map("team" -> tup._2, "alias" -> tup._1))

  def verify(t: Alias, u: Alias) = false
}
