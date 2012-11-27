package com.fijimf.deepfij.server.controller

import com.fijimf.deepfij.modelx.Quote

trait QuoteController {
  this: Controller =>

  get("/qotd") {
    qd.random().map(q=>{

    val id = "quote-%d".format(q.id)
      <p class="epigram" id={id} >
        {q.url
            {qd.random().map(q.quote).getOrElse("")}
          </p>
    })
    <p class="epigram" id="quote-">
      {qd.random().map(_.quote).getOrElse("")}
    </p>
  }

  post("/quote/new") {
    createQuote(params("quote"), params("source"), params("url"))
    redirect("/")
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
