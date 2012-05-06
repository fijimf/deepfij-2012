package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.modelx.TeamDao
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{AdminPanel, MissingResourcePanel, BasePage, TeamPanel}
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import com.fijimf.deepfij.workflow.Scraper

object AdminRoute extends Directives {
  val value=  Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))
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