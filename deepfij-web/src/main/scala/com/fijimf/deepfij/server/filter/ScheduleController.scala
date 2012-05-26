package com.fijimf.deepfij.server.filter

import java.util.Date
import org.apache.commons.lang.time.DateUtils
import com.fijimf.deepfij.workflow.{UpdateGamesAndResults, FullRebuild}
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{ScheduleListPanel, ScheduleCreatePanel, BasePage}

trait ScheduleController {
  this: Controller =>
  get("/schedule/new") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleCreatePanel())))
  }

  post("/schedule/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    redirect("/schedule/edit/" + params ("key"))
  }

  get("/schedule/makeprimary/:key") {
    val key: String = params("key")
    sd.setPrimary(key)
    log.info("Setting "+key+" as primary")
    redirect("/admin#collapseSchedules")
  }

  post("/schedule/rebuild") {
    rebuild(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))
  }

  post("/schedule/update") {
    update(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))

  }

  post("/schedule/delete") {
    delete(params("key"))
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleListPanel())))
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
