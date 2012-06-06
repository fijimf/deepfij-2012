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
import com.fijimf.deepfij.view._
import com.fijimf.deepfij.workflow.Scraper
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.text.SimpleDateFormat
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.WebUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx._

class Controller extends ScalatraFilter with ScheduleController{
  val log=Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()
  val std = new TeamStatDao()

  val scraper = Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))

  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  before() {
  }

  get("/") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "DeepFij", content = Some(<h1>Deep Fij</h1>)))
  }

  get("/date/:yyyymmdd") {

  }

  get("/team/:key") {
    contentType = "text/html"
    val key: String = params("key")
    html5Wrapper(td.findByKey(key) match {
      case Some(t) => BasePage(title = t.name, content = Some(TeamPanel(t)))
      case None => BasePage(title = "Team Not Found", content = Some(MissingResourcePanel("team", key)))
    })
  }

  get("/conference/:key") {
    contentType = "text/html"
    val key: String = params("key")
    html5Wrapper(cd.findByKey(key) match {
      case Some(c) => BasePage(title = c.name, content = Some(ConferencePanel(c)))
      case None => BasePage(title = "Conference Not Found", content = Some(MissingResourcePanel("conference", key)))
    })
  }


  get("/admin") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
  }



  get("/login") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Login", content = Some(LoginPanel())))
  }

  post("/login") {
    contentType = "text/html"
    SecurityUtils.getSubject.login(new UsernamePasswordToken(params("email"), params("password"), true))
    WebUtils.redirectToSavedRequest(request, response, "/login")
  }

  get("/logout") {
    logout
  }


  def logout: Any = {
    contentType = "text/html"
    SecurityUtils.getSubject.logout
    redirect("/")
  }

}