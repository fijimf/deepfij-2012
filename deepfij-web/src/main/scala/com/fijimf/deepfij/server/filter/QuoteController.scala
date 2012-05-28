package com.fijimf.deepfij.server.filter

import com.fijimf.deepfij.workflow.{UpdateGamesAndResults, FullRebuild}
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
    html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleCreatePanel())))
  }

  post("/quote/new") {
    create(params("key"), params("name"), params("from"), params("to"))
    redirect("/quote/show/" + params("key"))
  }

  get("/quote/show/:id") {
    contentType = "text/html"
    sd.findByKey(params("key")) match {
      case Some(schedule) => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleShowPanel(schedule))))
      case None => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", params("key")))))
    }
  }

  get("/quote/edit/:id") {
    contentType = "text/html"
    val key: String = params("id")
    sd.findByKey(key) match {
      case Some(schedule) => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(ScheduleEditPanel(schedule))))
      case None => html5Wrapper(BasePage(title = "Deep Fij Admin", content = Some(MissingResourcePanel("schedule", key))))
    }
  }
  post("/quote/edit") {
    contentType = "text/html"
    val key: String = params("id")
   redirect("/quote/show/"+id)
  }

  post("/quote/delete") {
    delete(params("id"))
    redirect("/admin#collapseQuotes")
  }
}
