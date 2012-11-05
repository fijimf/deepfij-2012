package com.fijimf.deepfij.workflow.datasource

import java.io.InputStream
import io.{Source, BufferedSource}
import com.fijimf.deepfij.modelx.{Schedule, Alias}
import java.util.Date
import org.apache.log4j.Logger

class TextAliasSource(parms: Map[String, String]) extends DataSource[Alias] {
  val log = Logger.getLogger(this.getClass)

  lazy val aliasList: List[(String, String)] = {
    val resource: String = parms("resource")
    log.info("Loading aliases from " + resource)
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(resource)
    val src: BufferedSource = Source.fromInputStream(is)
    val lst: List[(String, String)] = src.getLines().map(_.split(",")).map(arr => (arr(0), arr(1))).toList
    log.info(lst.size + " aliases loaded.")
    lst
  }

  def load = aliasList.map(tup => Map("key" -> tup._2, "alias" -> tup._1))

  def loadAsOf(date: Date) = aliasList.map(tup => Map("key" -> tup._2, "alias" -> tup._1))

  def build(schedule: Schedule, data: Map[String, String]) = {
    println(data)
    println(schedule.teamByKey.get(data("key")))
    for (k <- data.get("key");
         t <- schedule.teamByKey.get(k);
         a <- data.get("alias")) yield {
      println("K==> " + k)
      println("T==> " + t.name)
      println("A==> " + a)
      val alias: Alias = new Alias(schedule = schedule, team = t, alias = a)
      alias
    }
  }

  def update(t: Alias, data: Map[String, String]) = null

  def verify(t: Alias, u: Alias) = false
}
