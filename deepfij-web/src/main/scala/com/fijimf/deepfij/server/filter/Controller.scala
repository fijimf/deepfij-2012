/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/16/12
 * Time: 2:32 AM
 */
package com.fijimf.deepfij.server.filter

import org.scalatra.ScalatraFilter
import com.fijimf.deepfij.server.Util._
import org.apache.shiro.SecurityUtils
import cc.spray._
import http.MediaTypes._
import com.fijimf.deepfij.view.{ConferencePanel, MissingResourcePanel, TeamPanel, BasePage}
import com.fijimf.deepfij.modelx.{ConferenceDao, TeamDao}

class Controller extends ScalatraFilter {
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val scheduleKey = "2012"

  before() {
    println(SecurityUtils.getSubject.toString)
  }

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say
        <a href="hello-scalate">hello to Scalate</a>
        .
      </body>
    </html>
  }

  get("/team/:key") {
    contentType = "text/html"
    val key: String = params("key")
    println("Building Team Page " + scheduleKey + "," + key)
    html5Wrapper(td.findByKey(scheduleKey, key) match {
      case Some(t) => BasePage(title = t.name, content = Some(TeamPanel(t)))
      case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", key)))
    })
  }

  get("/conference/:key") {
    contentType = "text/html"
    val key: String = params("key")
    println("Building Conference Page " + scheduleKey + "," + key)
    html5Wrapper(cd.findByKey(scheduleKey, key) match {
      case Some(c) => BasePage(title = c.name, content = Some(ConferencePanel(c)))
      case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", key)))
    }
    )
  }

}