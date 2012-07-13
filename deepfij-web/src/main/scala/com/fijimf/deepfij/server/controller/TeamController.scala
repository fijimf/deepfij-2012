package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage}
import com.fijimf.deepfij.view.team.TeamPanel

trait TeamController {
  this: Controller =>
  get("/team/:key") {
    contentType = "text/html"
    td.findByKey(params("key")) match {
      case Some(t) => BasePage(title = t.name, content = Some(TeamPanel(t))).toHtml5()
      case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", params("key")))).toHtml5()
    }
  }


}
