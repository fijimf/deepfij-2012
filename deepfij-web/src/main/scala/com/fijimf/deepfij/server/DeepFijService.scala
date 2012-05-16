package com.fijimf.deepfij.server

import cc.spray._

import cc.spray.http.MediaTypes.`text/html`
import com.fijimf.deepfij.view._
import com.twitter.ostrich.admin.Service
import directives.SprayRoute0
import http.HttpMethod
import utils.Product0
import xml.NodeSeq
import com.fijimf.deepfij.modelx.{QuoteDao, ConferenceDao, TeamDao}
import java.text.SimpleDateFormat
import org.apache.log4j.Logger

trait DeepFijService extends Service with Directives {
  val logger = Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()

  def activeScheduleKey(): String

  lazy val service:Route = logPath {
    AdminRoute() ~
      path("date" / "[0-9]{8}".r) {
        d => cache {
          get {
            buildDatePage(d)
          }
        }
      } ~ (path("search") & parameter[String]("q")) {
      q => {
        val teams = td.search(q)
        respondWithMediaType(`text/html`) {
          _.complete(html5Wrapper(BasePage(title = "Search '" + q + "'", content = Some(SearchResultPanel(q, teams)))))
        }
      }
    } ~ path("quote") {
      get {
        respondWithMediaType(`text/html`) {
          _.complete {
            <p class="epigram">
              {qd.random().map(_.quote).getOrElse("")}
            </p>
          }
        }
      }
    } ~ path("login") {
      get {
        respondWithMediaType(`text/html`) {
          _.complete {
            "<!DOCTYPE html>\n" + BasePage(title = "Login", content = Some(LoginPanel())).toString()
          }
        }
      } ~ put {
        respondWithMediaType(`text/html`) {
          _.complete {
            "<!DOCTYPE html>\n" + BasePage(title = "Login", content = Some(LoginPanel())).toString()
          }
        }
      }
    }
  }

  def html5Wrapper(xml: NodeSeq): String = {
    "<!DOCTYPE html>\n" + xml.toString
  }

  def buildDatePage(d: String): Route = {
    val date = new SimpleDateFormat("yyyyMMdd").parse(d)
    val panel = Some(d, SchedulePanel(date))
    buildResourcePage(d, panel, "date")
  }


  def buildMissingResourcePage(resourceType: String, key: String): NodeSeq = {
    BasePage(title = "Not Found", content = Some(MissingResourcePanel(resourceType, key)))
  }

  def buildResourcePage(key: String, resource: Option[(String, NodeSeq)], resourceType: String): Route = {
    respondWithMediaType(`text/html`) {
      _.complete(
        html5Wrapper(resource.map(r => BasePage(title = r._1, content = Some(r._2))).getOrElse(buildMissingResourcePage(resourceType, key)))
      )
    }
  }


  val logPath = new SprayRoute0((ctx: RequestContext) => {
    logger.info(ctx.request.path)
    Pass
  })

}