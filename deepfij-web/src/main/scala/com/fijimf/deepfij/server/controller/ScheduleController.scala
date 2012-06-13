package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.workflow.{UpdateGamesAndResults, FullRebuild}
import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.schedule.{ScheduleEditPanel, ScheduleShowPanel, ScheduleCreatePanel}
import com.fijimf.deepfij.repo.StatisticRepository
import com.fijimf.deepfij.statx.models.{PointsModel, WonLostModel}
import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage}

trait ScheduleController {
  this: Controller =>

  val statRepo: StatisticRepository = new StatisticRepository

  get("/schedule/new") {
    contentType = "text/html"
    BasePage(title = "Deep Fij Admin", content = Some(ScheduleCreatePanel())).toHtml5()
  }

  post("/schedule/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    redirect("/schedule/show/" + params("key"))
  }

  get("/schedule/show/:key") {
    contentType = "text/html"
    sd.findByKey(params("key")) match {
      case Some(schedule) => BasePage(title = "Deep Fij Admin", content = Some(ScheduleShowPanel(schedule))).toHtml5()
      case None => BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", params("key")))).toHtml5()
    }
  }

  get("/schedule/edit/:key") {
    contentType = "text/html"
    sd.findByKey(params("key")) match {
      case Some(schedule) => BasePage(title = "Deep Fij Admin", content = Some(ScheduleEditPanel(schedule))).toHtml5()
      case None => BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", params("key")))).toHtml5()
    }
  }

  post("/schedule/rebuild") {
    rebuild(params("key"), params("from"), params("to"))
    redirect("/schedule/show/" + params("key"))
  }

  post("/schedule/results") {
    results(params("key"), params("from"), params("to"))
    redirect("/schedule/show/" + params("key"))
  }

  post("/schedule/rename") {
    rename(params("key"), params("name"))
    redirect("/schedule/show/" + params("key"))
  }

  post("/schedule/recalc") {
    recalc(params("key"))
    redirect("/schedule/show/" + params("key"))
  }

  post("/schedule/delete") {
    delete(params("key"))
    redirect("/admin#collapseSchedules")
  }

  post("/schedule/makeprimary") {
    sd.setPrimary(params("key"))
    redirect("/schedule/show/" + params("key"))
  }


  private def create(key: String, name: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    val cfg = FullRebuild(key, name, fromDate, toDate)
    scraper.scrape(cfg)
  }

  private def rebuild(key: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    for (s <- sd.findByKey(key)) {
      FullRebuild(key, s.name, fromDate, toDate)
    }
  }

  private def recalc(key: String) {
    sd.findByKey(key).map(s => {
      statRepo.publish(new WonLostModel().createStatistics(s))
      statRepo.publish(new PointsModel().createStatistics(s))
    })
  }


  private def results(key: String, from: String, to: String) {
    val fromDate = yyyymmdd.parse(from)
    val toDate = yyyymmdd.parse(to)
    scraper.scrape(UpdateGamesAndResults(key, fromDate, toDate))
  }

  private def rename(key: String, name: String) {
    sd.findByKey(key).map(s => {
      s.name = name
      sd.save(s)
    })
  }

  private def delete(key: String) {
    sd.findByKey(key).map(s => {
      sd.delete(s.id)
    })
  }


}
