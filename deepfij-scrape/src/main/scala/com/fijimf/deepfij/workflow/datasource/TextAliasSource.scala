package com.fijimf.deepfij.workflow.datasource

import java.io.InputStream
import io.{Source, BufferedSource}
import com.fijimf.deepfij.modelx.{Schedule, Alias}
import java.util.Date

class TextAliasSource(parms: Map[String, String]) extends DataSource[Alias] {
  lazy val aliasList: List[(String, String)] = {
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(parms("resource"))
    val src: BufferedSource = Source.fromInputStream(is)
    src.getLines().map(_.split(",")).map(arr => (arr(0), arr(1))).toList
  }

  def load = aliasList.map(tup => Map("key" -> tup._1, "alias" -> tup._2))

  def loadAsOf(date: Date) = aliasList.map(tup => Map("key" -> tup._1, "alias" -> tup._2))

  def build(schedule: Schedule, data: Map[String, String]) = {
    for (k <- data.get("key");
         t <- schedule.teamByKey.get(k);
         a <- data.get("alias")) yield new Alias(schedule = schedule, team = t, alias = a)
  }

  def update(t: Alias, data: Map[String, String]) = null

  def verify(t: Alias, u: Alias) = false
}
