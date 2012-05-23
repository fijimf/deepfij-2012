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
import java.util.Date
import org.apache.commons.lang.time.DateUtils
import com.fijimf.deepfij.modelx.{ScheduleDao, QuoteDao, ConferenceDao, TeamDao}
import com.fijimf.deepfij.workflow.{Scraper, UpdateGamesAndResults, FullRebuild}
import com.fijimf.deepfij.data.ncaa.NcaaTeamScraper
import com.fijimf.deepfij.data.kenpom.KenPomScraper
import java.text.SimpleDateFormat
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.WebUtils

class Controller extends ScalatraFilter {
  val td = new TeamDao()
  val cd = new ConferenceDao()
  val qd = new QuoteDao()
  val sd = new ScheduleDao()

  val scraper = Scraper(NcaaTeamScraper, NcaaTeamScraper, KenPomScraper("http://kenpom.com/cbbga12.txt", "kenpom.alias.txt"))

  val yyyymmdd = new SimpleDateFormat("yyyyMMdd")

  before() {
    println(SecurityUtils.getSubject.getPrincipal())
  }

  get("/") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "DeepFij", content = Some(<h1>Deep Fij</h1>)))
  }

  get("/admin") {

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

  get("/quote") {
    <p class="epigram">
      {qd.random().map(_.quote).getOrElse("")}
    </p>
  }


  get("/admin") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(AdminPanel())))
  }

  post("/admin/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))
  }
  post("/admin/rebuild") {
    rebuild(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))
  }

  post("/admin/update") {
    update(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))

  }
  post("/admin/delete") {
    delete(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))
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