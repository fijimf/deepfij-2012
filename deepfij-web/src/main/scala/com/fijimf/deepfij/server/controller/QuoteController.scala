package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.server.Util._
import com.fijimf.deepfij.view.{MissingResourcePanel, BasePage}
import com.fijimf.deepfij.view.schedule.{ScheduleEditPanel, ScheduleShowPanel, ScheduleCreatePanel}

trait QuoteController {
  this: Controller =>

  get("/qotd") {
    <p class="epigram">
      {qd.random().map(_.quote).getOrElse("")}
    </p>
  }


  get("/quote/new") {
    contentType = "text/html"
    BasePage(title = "Deep Fij Admin", content = Some(ScheduleCreatePanel())).toHtml5()
  }

  post("/quote/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    redirect("/quote/show/" + params("key"))
  }

  get("/quote/show/:id") {
    contentType = "text/html"
    sd.findByKey(params("key")) match {
      case Some(schedule) => BasePage(title = "Deep Fij Admin", content = Some(ScheduleShowPanel(schedule))).toHtml5()
      case None => BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", params("key")))).toHtml5()
    }
  }

  get("/quote/edit/:id") {
    contentType = "text/html"
    val key: String = params("id")
    sd.findByKey(key) match {
      case Some(schedule) => BasePage(title = "Deep Fij Admin", content = Some(ScheduleEditPanel(schedule))).toHtml5()
      case None => BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", key))).toHtml5()
    }
  }
  post("/quote/edit") {
    contentType = "text/html"
    val id: String = params("id")
    redirect("/quote/show/" + id)
  }

  post("/quote/delete") {
    deleteQuote(params("id"))
    redirect("/admin#collapseQuotes")
  }

  private[this] def deleteQuote(k: String) {

  }

  private[this] def create(a: String, b: String, c: String, d: String) {

  }
}
