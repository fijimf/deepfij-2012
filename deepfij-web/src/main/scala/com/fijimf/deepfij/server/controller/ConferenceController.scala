package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.view.{MissingResourcePanel, ConferencePanel, BasePage}

trait ConferenceController {
  this: Controller =>

  get("/conference/:key") {
    contentType = "text/html"
    schedule.conferenceByKey.get(params("key")) match {
      case Some(c) => BasePage(title = c.name, content = Some(ConferencePanel(c))).toHtml5()
      case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", params("key")))).toHtml5()
    }
  }
}