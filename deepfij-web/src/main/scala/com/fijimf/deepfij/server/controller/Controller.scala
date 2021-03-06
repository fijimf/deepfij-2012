package com.fijimf.deepfij.server.controller

import api.StatsController
import org.scalatra.ScalatraFilter
import org.apache.shiro.SecurityUtils
import java.text.SimpleDateFormat
import com.fijimf.deepfij.view.mappers._
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.WebUtils
import org.apache.log4j.Logger
import com.fijimf.deepfij.modelx._
import org.scalatra.scalate.ScalateSupport
import java.util.{Calendar, TimerTask, Timer, Date}
import scala.Some
import org.apache.commons.lang.time.DateUtils
import scala.util.control.Exception._
import com.codahale.jerkson.Json

class Controller extends ScalatraFilter with ScalateSupport with StatsController {
  val log = Logger.getLogger(this.getClass)
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()
  val std = new TeamStatDao()
  val msd: MetaStatDao = new MetaStatDao()

  var schedule = sd.findPrimary().get

  val statList: List[String] = msd.findAll().map(_.statKey)

  var stats = statList.map(k => k -> catching(classOf[Exception]).opt({
    std.population(k, DateUtils.truncate(new Date(), Calendar.DATE))
  })).filter(_._2.isDefined).map(p => (p._1, p._2.get)).toMap

  def attributes(): Map[String, Any] = {
    val m: Map[String, Any] = Map("ctx" -> contextPath, "quote" -> qd.random().getOrElse(new Quote(quote = "How bad it gets you can't imagine; the burning wax, the breath of reptiles.", source = "Shriekback", url = "http://www.mofito.com/music-videos/shriekback/6957067-nemesis.htm"))) ++ SubjectMapper(SecurityUtils.getSubject)
    log.info("Base attributes are " + m)
    m
  }

  //TODO -- Make this better

  override def contextPath: String = {
    request.getContextPath
  }

  new Timer("reloadSchedule").schedule(new TimerTask {
    def run() {
      sd.entityManager.clear()
      schedule = sd.findPrimary().get
      stats = statList.map(k => k -> std.population(k, DateUtils.truncate(new Date(), Calendar.DATE))).toMap
    }
  }, 30000L, 3600000L)
  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  get("/") {
    contentType = "text/html"

    val zzz = stats.mapValues(pop => Map("name" -> pop.name, "mean" -> pop.mean,
      "top25" -> pop.topN(20).map(tup => Map(
        "name" -> tup._1.name, "key" -> tup._1.key, "value" -> pop.format.format(tup._2), "rank" -> pop.rank(tup._1)))))
    templateEngine.layout("pages/home.mustache", zzz ++ attributes())
  }

  get("/date/:yyyymmdd") {
    contentType = "text/html"
    templateEngine.layout("pages/date.mustache", attributes() ++ DateMapper(schedule, yyyymmdd.parse(params("yyyymmdd")), stats))
  }

  get("/team/:key") {
    contentType = "text/html"
    schedule.teamByKey.get(params("key")) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", attributes() ++ TeamMapper(t, stats))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "team", "key" -> params("key")))
    }
  }

  get("/team/:schedule/:key") {
    contentType = "text/html"
    sd.findByKey(params("schedule")).flatMap(_.teamByKey.get(params("key"))) match {
      case Some(t) => templateEngine.layout("pages/team.mustache", attributes() ++ TeamMapper(t, stats))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "team", "key" -> params("key")))
    }
  }

  get("/stat/:key") {
    contentType = "text/html"
    stats.get(params("key")) match {
      case Some(s) => templateEngine.layout("pages/stat.mustache", attributes() ++ StatMapper(s))
      case None => templateEngine.layout("pages/notfound.mustache", attributes() ++ Map("title" -> "Not Found", "resource" -> "statistic", "key" -> params("key")))
    }
  }

  get("/stat/:key/api") {
    contentType = "application/json"
    stats.get(params("key")) match {
      case Some(s) => {
        val map: Map[String, Object] = StatMapper(s)
        Json.generate(map)
      }
      case None => {
        "[]"

      }
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
      redirect("%s/date/%s".format(contextPath, yyyymmdd.format(dates.head)))
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
    contentType = "text/html"
    SecurityUtils.getSubject.logout()
    redirect("/" + contextPath)
  }

  get("/about") {
    contentType = "text/html"
    templateEngine.layout("pages/about.mustache", attributes())
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
    templateEngine.layout("pages/404.mustache", attributes())
  }
}