package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.modelx.Quote

trait QuoteController {
  this: Controller =>

  get("/qotd") {
    <p class="epigram">
      {qd.random().map(_.quote).getOrElse("")}
    </p>
  }

  get("/quote/list") {
    contentType = "text/html"
    templateEngine.layout("pages/quotelist.mustache", Map("quotes" -> qd.findAll().map(q => Map("id" -> q.id, "quote" -> q.quote, "source" -> q.source, "url" -> q.url))))
  }

  post("/quote/new") {
    createQuote(params("quote"), params("source"), params("url"))
    redirect("/quote/list")
  }

  post("/quote/edit") {
    contentType = "text/html"
    val id: String = params("id")
    redirect("/quote/show/" + id)
  }

  post("/quote/delete") {
    deleteQuote(params("id").toLong)
    redirect("/admin#collapseQuotes")
  }

  def deleteQuote(id: Long) {

  }

  def createQuote(q: String, s: String, u: String) = {
    qd.save(new Quote(quote = q, source = s, url = u))
  }

}
