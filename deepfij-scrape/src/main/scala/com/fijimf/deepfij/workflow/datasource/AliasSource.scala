package com.fijimf.deepfij.workflow.datasource

import com.fijimf.deepfij.util.Util._
import java.util.Date
import com.fijimf.deepfij.modelx._
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper

class AliasSource() extends DataSource[Alias] {
  val aliasDao = new AliasDao

  def load: List[Map[String, String]] = {
    List.empty[Map[String, String]]
  }

  def update(date: Date): List[Map[String, String]] = {
    List.empty[Map[String, String]]
  }

  def build(schedule: Schedule, data: Map[String, String]): Option[Alias] = {
    //TODO fixme
    None
  }

  def update(a: Alias, data: Map[String, String]): Alias = {
    //TODO fixme
    a
  }

  def loadAsOf(date: Date) = List.empty[Map[String, String]]

  def verify(t: Alias, u: Alias) = false

}
