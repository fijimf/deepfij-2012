package com.fijimf.deepfij.data.exporter

import com.fijimf.deepfij.modelx.Team
import com.fijimf.deepfij.workflow.datasource.{TeamBuilder, Exporter}
import java.io.FileInputStream
import com.fijimf.deepfij.util.Logging

class TeamExporter(parms: Map[String, String]) extends Exporter[Team] with TeamBuilder with Logging {

  lazy val inputStream = new FileInputStream(parms("fileName"))

  def fromString(s: String): Map[String, String] = {
    def ident(m: Map[String, String]): Map[String, String] = m
    s.split('|').toList match {
      case key :: name :: conference :: longName :: logo :: nickname :: officialUrl :: primaryColor :: secondaryColor :: tail => {
        (ident _).andThen(addNotBlank(_, "nickname", nickname)).
          andThen(addNotBlank(_, "logo", logo)).
          andThen(addNotBlank(_, "officialUrl", officialUrl)).
          andThen(addNotBlank(_, "primaryColor", primaryColor)).
          andThen(addNotBlank(_, "secondaryColor", secondaryColor)).
          apply(Map("key" -> key, "name" -> name, "conference" -> conference, "longName" -> longName))
      }
      case _ => Map.empty[String, String]
    }
  }

  def toString(t: Team): String = {
    t.key + "|" + t.name + "|" + t.conference.key + "|" + t.longName + "|" + t.logoOpt.getOrElse("") + "|" + t.nicknameOpt.getOrElse("") + "|" + t.officialUrlOpt.getOrElse("") + "|" + t.primaryColorOpt.getOrElse("") + "|" + t.secondaryColorOpt.getOrElse("")
  }

  def update(t: Team, data: Map[String, String]) = null

  def verify(t: Team, u: Team) = false
}
