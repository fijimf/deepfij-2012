package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage}
import com.fijimf.deepfij.view.mappers.{SubjectMapper, TeamMapper}
import org.apache.shiro.SecurityUtils

trait TeamController {
  this: Controller =>
  get("/team/:key") {
    contentType = "text/html"
    schedule.teamByKey.get(params("key")) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", TeamMapper(t) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", params("key")))).toHtml5()
    }
  }

  get("/team/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.teamByKey.get(params("key"))) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", TeamMapper(t) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", params("key")))).toHtml5()
    }
  }

}
