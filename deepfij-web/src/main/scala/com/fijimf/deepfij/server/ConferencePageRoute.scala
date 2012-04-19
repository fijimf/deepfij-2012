package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.ConferencePanel._
import java.text.SimpleDateFormat
import com.fijimf.deepfij.view.SchedulePanel._
import xml.NodeSeq
import com.fijimf.deepfij.modelx.{ConferenceDao, TeamDao}
import com.fijimf.deepfij.view.{ConferencePanel, MissingResourcePanel, BasePage, TeamPanel}

object ConferencePageRoute extends Directives {
  val cd = new ConferenceDao()

  def apply(scheduleKey: String): Route = {

    path("conference" / "[a-z-]+".r) {
       key => cache {
         get {
           buildConferencePage(scheduleKey, key)
         }
       }
     }
  }

  def buildConferencePage(scheduleKey:String, key:String):Route = {
    respondWithMediaType(`text/html`) {
      _.complete(
        html5Wrapper(cd.findByKey(scheduleKey, key) match {
          case Some(c) => BasePage(title = c.name, content = Some(ConferencePanel(c)))
          case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", key)))
        })
      )
    }

  }
}