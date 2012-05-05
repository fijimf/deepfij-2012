package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.modelx.TeamDao
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{AdminPanel, MissingResourcePanel, BasePage, TeamPanel}

object AdminRoute extends Directives {
  def apply(): Route = {
    pathPrefix("admin") {
      path("new") {
        get {
          respondWithMediaType(`text/html`) {
            _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
            )
          }
      }
      }~ path("rebuild"){
        get {
          respondWithMediaType(`text/html`) {
            _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
            )
          }
        }
      } ~
      get {
        respondWithMediaType(`text/html`) {
          _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
          )
        }
      }
    }
  }
}