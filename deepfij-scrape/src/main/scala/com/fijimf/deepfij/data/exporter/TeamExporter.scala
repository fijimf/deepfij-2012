package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.Team
import com.fijimf.deepfij.workflow.{Initializer, Exporter}
import com.fijimf.deepfij.workflow.datasource.TeamBuilder
import com.fijimf.deepfij.util.Logging

class TeamExporter(parms: Map[String, String]) extends Exporter[Team] with TeamBuilder with Initializer[Team] with Logging {

  def fileName = parms("fileName")

  def dataDir = parms("dataDir")

  def load = data

  def fromString(s: String): Map[String, String] = {
    def ident(m: Map[String, String]): Map[String, String] = m
    s.split('|').toList.padTo(9, "") match {
      case key :: name :: conference :: longName :: logo :: nickname :: officialUrl :: primaryColor :: secondaryColor :: tail => {
        (ident _).andThen(addNotBlank(_, "nickname", nickname)).
          andThen(addNotBlank(_, "logo", logo)).
          andThen(addNotBlank(_, "officialUrl", officialUrl)).
          andThen(addNotBlank(_, "primaryColor", primaryColor)).
          andThen(addNotBlank(_, "secondaryColor", secondaryColor)).
          apply(Map("key" -> key, "name" -> name, "conference" -> conference, "longName" -> longName))
      }
      case _ => {
        log.error("Cannot parse team=" + s)
        log.error(s.split('|').toList.padTo(9, ""))
        Map.empty[String, String]
      }
    }
  }

  def toString(t: Team): String = {
    t.key + "|" + t.name + "|" + t.conference.key + "|" + t.longName + "|" + t.logoOpt.getOrElse("") + "|" + t.nicknameOpt.getOrElse("") + "|" + t.officialUrlOpt.getOrElse("") + "|" + t.primaryColorOpt.getOrElse("") + "|" + t.secondaryColorOpt.getOrElse("") + "|"
  }

}
