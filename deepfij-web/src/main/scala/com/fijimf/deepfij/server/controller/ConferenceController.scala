package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.view.MissingResourcePanel
import com.fijimf.deepfij.view.mappers.{SubjectMapper, ConferenceMapper}
import org.apache.shiro.SecurityUtils

trait ConferenceController {
  this: Controller =>


  get("/conference/:key") {
    contentType = "text/html"
    schedule.conferenceByKey.get(params("key")) match {

      case Some(c) => templateEngine.layout("pages/conference.mustache", Map("ctx" -> request.getContextPath) ++ ConferenceMapper(c) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => templateEngine.layout("pages/notfound.mustache", Map("ctx" -> request.getContextPath) ++ SubjectMapper(SecurityUtils.getSubject)++Map("title"->"Not Found", "resource"->"conference", "key"->params("key")))
    }
  }

  get("/conference/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.conferenceByKey.get(params("key"))) match {
      case Some(c) => templateEngine.layout("pages/conferences.mustache", Map("ctx" -> request.getContextPath) ++ ConferenceMapper(c) ++ SubjectMapper(SecurityUtils.getSubject))
      case None => templateEngine.layout("pages/notfound.mustache", Map("ctx" -> request.getContextPath) ++ SubjectMapper(SecurityUtils.getSubject)++Map("title"->"Not Found", "resource"->"conference", "key"->params("key")))
    }
  }

}