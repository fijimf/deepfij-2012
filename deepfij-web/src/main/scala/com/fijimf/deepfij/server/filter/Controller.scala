/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/16/12
 * Time: 2:32 AM
 */
package com.fijimf.deepfij.server.filter

import org.scalatra.ScalatraFilter
import com.fijimf.deepfij.modelx.TeamDao
import com.fijimf.deepfij.view.{MissingResourcePanel, TeamPanel, BasePage}
import com.fijimf.deepfij.server.Util._

class Controller extends ScalatraFilter {
  val td = new TeamDao()
  val scheduleKey = "2012"

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
}