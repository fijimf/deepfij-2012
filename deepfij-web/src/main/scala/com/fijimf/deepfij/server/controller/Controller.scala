/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/16/12
 * Time: 2:32 AM
 */
package com.fijimf.deepfij.server.controller

import api.StatsController
import org.scalatra.ScalatraFilter
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

class Controller extends ScalatraFilter with ScheduleController with TeamController with ConferenceController with StatsController {
  val log = Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()
  val std = new TeamStatDao()

  val scraper = Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))

  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  get("/") {
    contentType = "text/html"
    BasePage(title = "DeepFij", content = Some(<h1>Deep Fij</h1>)).toHtml5()
  }

  get("/date/:yyyymmdd") {

  }




  get("/admin") {
    contentType = "text/html"
    BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())).toHtml5()
  }



  get("/login") {
    contentType = "text/html"
    BasePage(title = "Login", content = Some(LoginPanel())).toHtml5()
  }

  post("/login") {
    contentType = "text/html"
    SecurityUtils.getSubject.login(new UsernamePasswordToken(params("email"), params("password"), true))
    WebUtils.redirectToSavedRequest(request, response, "/login")
  }

  get("/logout") {
    logout
  }

  get("/scripts/*") {
    filterChain.doFilter(request, response)
  }

  get("/style/*") {
    filterChain.doFilter(request, response)
  }

  get("/images/*") {
    filterChain.doFilter(request, response)
  }

  notFound {
    contentType = "text/html"
    status(404)
    BasePage(title = "Not Found", content = Some(<h1>Not Found</h1>)).toHtml5()
  }


  def logout: Any = {
    contentType = "text/html"
    SecurityUtils.getSubject.logout()
    redirect("/")
  }

}