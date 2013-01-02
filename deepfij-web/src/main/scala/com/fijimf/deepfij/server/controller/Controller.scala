package com.fijimf.deepfij.server.controller

import api.StatsController
import org.scalatra.ScalatraFilter
import org.apache.shiro.SecurityUtils
import com.fijimf.deepfij.view._
import java.text.SimpleDateFormat
import com.fijimf.deepfij.view.mappers.{ConferenceMapper, SearchMapper, SubjectMapper, TeamMapper}
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.WebUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx._
import org.scalatra.scalate.ScalateSupport
import java.util.Date

class Controller extends ScalatraFilter with ScalateSupport with TeamController with ConferenceController with StatsController with QuoteController {
  val log = Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()
  val std = new TeamStatDao()

  lazy val schedule = sd.findPrimary().get

  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  get("/") {
    contentType = "text/html"
    templateEngine.layout("pages/home.mustache", Map("ctx" -> request.getContextPath) ++ SubjectMapper(SecurityUtils.getSubject))
  }

  get("/date/:yyyymmdd") {

  }

  get("/admin") {
    contentType = "text/html"
    templateEngine.layout("pages/admin.mustache", Map("ctx" -> request.getContextPath) ++ SubjectMapper(SecurityUtils.getSubject) ++Map("title"-> "Admin"))
  }

  get("/search") {
    contentType = "text/html"
    val results: Map[String, Object] = SearchMapper(schedule, params("q"))
    val teams: List[Team] = results("teams").asInstanceOf[List[Team]]
    val conferences: List[Team] = results("conferences").asInstanceOf[List[Conference]]
    val dates: List[Date] = results("dates").asInstanceOf[List[Team]]
    if (teams.size==1) {
      templateEngine.layout("pages/team.mustache", Map("ctx" -> request.getContextPath) ++ TeamMapper(teams.head) ++ SubjectMapper(SecurityUtils.getSubject))
    } else if (conferences.size==1 && teams.size==0) {
      templateEngine.layout("pages/conference.mustache", Map("ctx" -> request.getContextPath) ++ ConferenceMapper(conferences.head) ++ SubjectMapper(SecurityUtils.getSubject))
    } else if (dates.size==1) {
      templateEngine.layout("pages/date.mustache", Map("ctx" -> request.getContextPath) ++ DateMapper(dates.head) ++ SubjectMapper(SecurityUtils.getSubject))
    } else {
      templateEngine.layout("pages/searchresults.mustache", Map("ctx" -> request.getContextPath) ++ results++ SubjectMapper(SecurityUtils.getSubject))
    }

  }

  get("/login") {
    contentType = "text/html"
    BasePage(title = "Login", content = Some(LoginPanel())).toHtml5()
  }

  post("/login") {
    contentType = "text/html"
    SecurityUtils.getSubject.login(new UsernamePasswordToken(params("email"), params("password"), true))
    WebUtils.redirectToSavedRequest(request, response, "/")
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