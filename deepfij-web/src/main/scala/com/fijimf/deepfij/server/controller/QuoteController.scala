package com.fijimf.deepfij.server.controller

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

  get("/quote/list") {
    contentType = "text/html"
    templateEngine.layout("pages/quotelist.mustache", Map("quotes" -> qd.findAll().map(q => Map("id" -> q.id, "quotes" -> q.quote, "source" -> q.source, "url" -> q.url))))
  }

  post("/quote/new") {
    val id = createQuote(params("quote"), params("source"), params("url"))
    redirect("/quote/show/" + id)
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

  def deleteQuote(k: String) {

  }

  def createQuote(q: String, s: String, u: String): Integer = {
    0
  }

}
