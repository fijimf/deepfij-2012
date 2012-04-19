package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage, TeamPanel}
import com.fijimf.deepfij.modelx.TeamDao
import com.fijimf.deepfij.server.Util._

object TeamPageRoute extends Directives {
  val td = new TeamDao()

  def apply(scheduleKey: String): Route = {
    path("team" / "[a-z-]+".r) {
      key => cache {
        get {
          println("Building Team Page " + scheduleKey + "," + key)
          buildTeamPage(scheduleKey, key)
        }
      }
    }
  }

  def buildTeamPage(scheduleKey: String, key: String): Route = {
    respondWithMediaType(`text/html`) {
      _.complete(
        html5Wrapper(td.findByKey(scheduleKey, key) match {
          case Some(t) => BasePage(title = t.name, content = Some(TeamPanel(t)))
          case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", key)))
        })
      )
    }
  }
}