package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.view.{MissingResourcePanel, ConferencePanel, BasePage}
import com.fijimf.deepfij.view.mappers.{SubjectMapper, ConferenceMapper, TeamMapper}
import org.apache.shiro.SecurityUtils

trait ConferenceController {
  this: Controller =>


  get("/conference/:key") {
    contentType = "text/html"
    schedule.conferenceByKey.get(params("key")) match {

      case Some(c) => templateEngine.layout("pages/conference.mustache", Map("ctx" -> request.getContextPath) ++ ConferenceMapper(c) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", params("key")))).toHtml5()
    }
  }

  get("/conference/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.conferenceByKey.get(params("key"))) match {
      case Some(c) => templateEngine.layout("pages/conferences.mustache", Map("ctx" -> request.getContextPath) ++ ConferenceMapper(c) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", params("key")))).toHtml5()
    }
  }

}