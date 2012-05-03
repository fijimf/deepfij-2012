package com.fijimf.deepfij.data.ncaa.json

case class Game(id: String, conference: String, gameState: String, startDate: String, startDateDisplay: String, startTime: String,
                currentPeriod: String, timeclock: String, network_logo: String, location: String,
                url: String, highlightsUrl: String, gameCenterUrl: String, scoreBreakdown: Option[List[String]], home: Team, away: Team, tabs: String) {
  override def toString: String = {
    id + " " + gameState + " " + currentPeriod + " " + timeclock + " " + scoreBreakdown + " " + home + " " + away
  }
}
