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
    log.info(lst.size + " potential aliases loaded.")
    lst
  }

  def load = aliasList.map(tup => Map("key" -> tup._2, "alias" -> tup._1))

  def loadAsOf(date: Date) = aliasList.map(tup => Map("key" -> tup._2, "alias" -> tup._1))

  def build(schedule: Schedule, data: Map[String, String]) = {
    val key = data("key")
    val alias = data("alias")
    require(Option(key).isDefined && Option(alias).isDefined)
    val teamOption = schedule.teamByKey.get(key)
    if (teamOption.isDefined){
      if (teamOption.get.name==alias){
        log.info("Skipping "+alias+" -> "+alias+" because the alias is the same as the team name")
        None
      } else {
        Some(new Alias(schedule = schedule, team = teamOption.get, alias = alias))
      }
    } else {
      log.info("No team found for key "+key)
       None
    }
  }

  def update(t: Alias, data: Map[String, String]) = null

  def verify(t: Alias, u: Alias) = false
}
