package com.fijimf.deepfij.data

trait AliasStrategy {
  def createAliasesValues(teamData: List[Map[String, String]], dirtyNames: List[(String, Double)]): Map[String, String]
}
