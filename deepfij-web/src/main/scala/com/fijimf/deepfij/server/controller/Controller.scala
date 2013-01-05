package com.fijimf.deepfij.server.controller

import api.StatsController
import org.scalatra.ScalatraFilter
import org.apache.shiro.SecurityUtils
import java.text.SimpleDateFormat
import com.fijimf.deepfij.view.mappers._
import org.apache.shiro.authc.{UnknownAccountException, UsernamePasswordToken}
import org.apache.shiro.web.util.WebUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx._
import org.scalatra.scalate.ScalateSupport
import java.util.{TimerTask, Timer, Date}
import com.fijimf.deepfij.view.BasePage
import scala.Some

class Controller extends ScalatraFilter with ScalateSupport with ConferenceController with StatsController {
  val log = Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()
  val std = new TeamStatDao()

  var schedule = sd.findPrimary().get

  def attributes(): Map[String, Any] = {
    val m: Map[String, Any] = Map("ctx" -> contextPath, "quote" -> qd.random().getOrElse(new Quote(quote = "How bad it gets you can't imagine; the burning wax, the breath of reptiles.", source = "Shriekback", url = "http://www.mofito.com/music-videos/shriekback/6957067-nemesis.htm"))) ++ SubjectMapper(SecurityUtils.getSubject)
    log.info("Base attributes are " + m)
    m
  }

  //TODO -- Make this better

  def contextPath: String = {
    request.getContextPath
  }

  new Timer("reloadSchedule").schedule(new TimerTask {
    def run() {
      schedule = sd.findPrimary().get
    }
  }, 30000L, 3600000L)
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  get("/") {
    contentType = "text/html"
    templateEngine.layout("pages/home.mustache", attributes())
  }

  get("/date/:yyyymmdd") {
    contentType = "text/html"
    templateEngine.layout("pages/date.mustache", attributes() ++ DateMapper(schedule, yyyymmdd.parse(params("yyyymmdd"))))
  }

  get("/team/:key") {
    contentType = "text/html"
    schedule.teamByKey.get(params("key")) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", attributes() ++ TeamMapper(t))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "team", "key" -> params("key")))
    }
  }

  get("/team/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.teamByKey.get(params("key"))) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", attributes() ++ TeamMapper(t))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "team", "key" -> params("key")))
    }
  }

  get("/conference/:key") {
    contentType = "text/html"
    schedule.conferenceByKey.get(params("key")) match {

      case Some(c) => templateEngine.layout("pages/conference.mustache", attributes() ++ ConferenceMapper(c))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "conference", "key" -> params("key")))
    }
  }

  get("/conference/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.conferenceByKey.get(params("key"))) match {
      case Some(c) => templateEngine.layout("pages/conferences.mustache", attributes() ++ ConferenceMapper(c))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "conference", "key" -> params("key")))
    }
  }

  get("/admin") {
    contentType = "text/html"
    templateEngine.layout("pages/admin.mustache", attributes() ++ Map("title" -> "Admin"))
  }

  get("/search") {
    contentType = "text/html"
    val results: Map[String, Object] = SearchMapper(schedule, params("q"))
    val teams: List[Team] = results("teams").asInstanceOf[List[Team]]
    val conferences: List[Conference] = results("conferences").asInstanceOf[List[Conference]]
    val dates: List[Date] = results("dates").asInstanceOf[List[Date]]
    if (teams.size == 1) {
      redirect("%s/team/%s".format(contextPath, teams.head.key))
    } else if (conferences.size == 1 && teams.size == 0) {
      redirect("%s/conference/%s".format(contextPath, conferences.head.key))
    } else if (dates.size == 1) {
      redirect("%s/date/%s".format(yyyymmdd.format(contextPath, dates.head)))
    } else {
      templateEngine.layout("pages/searchresults.mustache", attributes() ++ results)
    }

  }

  get("/login") {
    contentType = "text/html"
    templateEngine.layout("pages/login.mustache", attributes() ++ Map("title" -> "Login"))
  }

  post("/login") {
    contentType = "text/html"
    try {
      SecurityUtils.getSubject.login(new UsernamePasswordToken(params("email"), params("password"), true))
      WebUtils.redirectToSavedRequest(request, response, "/")
    }
    catch {
      case ex: RuntimeException => templateEngine.layout("pages/login.mustache", attributes() ++ Map("title" -> "Login", "loginFailed" -> true, "email" -> params("email")))
    }
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