package com.fijimf.deepfij.data.ncaa.json

import java.util.regex.Pattern


case class Team(teamRank: String, iconURL: String, name: String, shortname: String, description: String,
                currentScore: String, scoreBreakdown: List[String], winner: String) {

  def breakoutName(i: Int): String = {
    try {
      val p = Pattern.compile("<a href='/schools/(\\S+)'>(.+)</a>")
      val m = p.matcher(name)
      m.matches()

      m.group(i)
    }
    catch {
      case e: IllegalStateException => {
        "Could not find key for " + name
        name
      }
    }
  }

  def key = {
    breakoutName(1)
  }

  def canonicalName = {
    breakoutName(2)
  }

  //  val key = (XML.loadString(name) \ "@href").text.split("/").last

  override def toString: String = {
    key + " " + currentScore + " " + scoreBreakdown
  }
}

