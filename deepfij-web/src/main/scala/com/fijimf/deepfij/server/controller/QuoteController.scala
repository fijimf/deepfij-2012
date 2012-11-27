package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.modelx.Quote

trait QuoteController {
  this: Controller =>

  get("/qotd") {
    <p class="epigram">
      {qd.random().map(_.quote).getOrElse("")}
    </p>
  }

  post("/quote/new") {
    createQuote(params("quote"), params("source"), params("url"))
    redirect("/")
  }

  def createQuote(q: String, s: String, u: String) = {
    qd.save(new Quote(quote = q, source = s, url = u))
  }

}
