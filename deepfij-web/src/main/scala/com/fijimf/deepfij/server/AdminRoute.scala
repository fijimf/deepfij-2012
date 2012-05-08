package com.fijimf.deepfij.server

import cc.spray._
import cc.spray.http.MediaTypes.`text/html`
import cc.spray.Route
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{AdminPanel, BasePage}
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import org.apache.log4j.Logger
import java.text.SimpleDateFormat
import com.fijimf.deepfij.modelx.ScheduleDao
import java.util.Date
import com.fijimf.deepfij.workflow.{UpdateGamesAndResults, FullRebuild, Scraper}
import org.apache.commons.lang.time.DateUtils

object AdminRoute extends Directives {
  val log = Logger.getLogger(this.getClass)
  val scraper = Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))
  val sd = new ScheduleDao()

  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  def apply(): Route = {
    pathPrefix("admin") {
      path("new") {
        post {
          formFields('key, 'name, 'from, 'to) {
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
      } ~ path("rebuild") {
        post {
          formFields('key) {
            (key: String) => {
              log.info("Key=" + key)
              rebuild(key)
              respondWithMediaType(`text/html`) {
                _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
                )
              }
            }
          }
        }
      } ~ path("update") {
        post {
          formFields('key) {
            (key: String) => {
              log.info("Key=" + key)
              update(key)
              respondWithMediaType(`text/html`) {
                _.complete(html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
                )
              }
            }
          }
        }
      } ~ path("delete") {
        post {
          formFields('key) {
            (key: String) => {
              log.info("Key=" + key)
              delete(key)
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

  def rebuild(key: String) {
    val cfg = sd.findByKey(key).map(s => {
      val fromDate = s.gameList match {
        case Nil => new Date
        case lst => lst.minBy(_.date).date
      }
      val toDate = DateUtils.addDays(new Date, 1)
      FullRebuild(key, s.name, DateUtils.addDays(fromDate, -7), toDate)
    })
    cfg.map(scraper.scrape(_))
  }


  def update(key: String) {
    val cfg = sd.findByKey(key).map(s => {
      val fromDate = s.gameList match {
        case Nil => new Date
        case lst => lst.minBy(_.date).date
      }
      val toDate = DateUtils.addDays(new Date, 1)
      UpdateGamesAndResults(key, fromDate, toDate)
    })
    cfg.map(scraper.scrape(_))
  }

  def delete(key: String) {
    sd.findByKey(key).map(s => {
      sd.delete(s.id)
    })
  }
}

