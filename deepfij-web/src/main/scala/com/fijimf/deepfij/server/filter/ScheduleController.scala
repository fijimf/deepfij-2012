package com.fijimf.deepfij.server.filter

import com.fijimf.deepfij.workflow.{UpdateGamesAndResults, FullRebuild}
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage}
import com.fijimf.deepfij.view.schedule.{ScheduleEditPanel, ScheduleShowPanel, ScheduleCreatePanel}

trait ScheduleController {
  this: Controller =>

  get("/schedule/new") {
    contentType = "text/html"
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleCreatePanel())))
  }

  post("/schedule/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    redirect("/schedule/show/" + params("key"))
  }

  get("/schedule/show/:key") {
    contentType = "text/html"
    sd.findByKey(params("key")) match {
      case Some(schedule) => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleShowPanel(schedule))))
      case None => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", params("key")))))
    }
  }

  get("/schedule/edit/:key") {
    contentType = "text/html"
    val key: String = params("key")
    sd.findByKey(key) match {
      case Some(schedule) => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleEditPanel(schedule))))
      case None => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", key))))
    }
  }

  post("/schedule/rebuild") {
    val key: String = params("key")
    rebuild(key, params("from"), params("to"))
    redirect("/schedule/show/" + key)
  }

  post("/schedule/results") {
    val key: String = params("key")
    results(key, params("from"), params("to"))
    redirect("/schedule/show/" + key)
  }
  post("/schedule/rename") {
    val key: String = params("key")
    rename(key, params("name"))
    redirect("/schedule/show/" + key)
  }
  post("/schedule/recalc") {
    val key: String = params("key")
    recalc(key, params("from"), params("to"))
    redirect("/schedule/show/" + key)
  }

  post("/schedule/delete") {
    delete(params("key"))
    redirect("/admin#collapseSchedules")
  }

  post("/schedule/makeprimary") {
    val key: String = params("key")
    sd.setPrimary(key)
    log.info("Setting " + key + " as primary")
    redirect("/schedule/show/" + key)
  }


  def create(key: String, name: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    val cfg = FullRebuild(key, name, fromDate, toDate)
    scraper.scrape(cfg)
  }

  def rebuild(key: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    for (s <- sd.findByKey(key)) {
      FullRebuild(key, s.name, fromDate, toDate)
    }
  }

  def recalc(key: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    //    scraper.scrape(UpdateGamesAndResults(key, fromDate, toDate))
  }


  def results(key: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    scraper.scrape(UpdateGamesAndResults(key, fromDate, toDate))
  }

  def rename(key: String, name: String) {
    sd.findByKey(key).map(s => {
      s.name = name
      sd.save(s)
    })
  }

  def delete(key: String) {
    sd.findByKey(key).map(s => {
      sd.delete(s.id)
    })
  }


}
