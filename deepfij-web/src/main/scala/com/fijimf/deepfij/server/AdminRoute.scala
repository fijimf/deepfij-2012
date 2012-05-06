package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{AdminPanel, BasePage}
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import org.apache.log4j.Logger
import com.fijimf.deepfij.workflow.{FullRebuild, Scraper}
import java.text.SimpleDateFormat

object AdminRoute extends Directives {
  val log = Logger.getLogger(this.getClass)
  val scraper = Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def apply(): Route = {
    pathPrefix("admin") {
      path("new") {
        get {
          detach {
            parameters('key, 'name, 'from, 'to) {
              (key: String, name: String, from: String, to: String) => {
                log.info("Key=" + key)
                log.info("Name=" + name)
                log.info("From=" + from)
                log.info("To=" + to)
                create(key, name, from, to)
                respondWithMediaType(`text/html`) {
                  _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
                  )
                }
              }
            }
          }
        }
      } ~ path("rebuild") {
        get {
          parameters('key, 'name, 'from, 'to) {
            (key: String, name: String, from: String, to: String) => {
              log.info("Key=" + key)
              log.info("Name=" + name)
              log.info("From=" + from)
              log.info("To=" + to)
              respondWithMediaType(`text/html`) {
                _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
                )
              }
            }
          }
        }
      } ~ get {
        respondWithMediaType(`text/html`) {
          _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
          )
        }
      }
    }
  }


  def create(key: String, name: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    val cfg = FullRebuild(key, name, fromDate, toDate)
    scraper.scrape(cfg)
  }


  def update(key: String, name: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    val cfg = FullRebuild(key, name, fromDate, toDate)
    scraper.scrape(cfg)
  }

  def delete(key: String) {

  }
}

