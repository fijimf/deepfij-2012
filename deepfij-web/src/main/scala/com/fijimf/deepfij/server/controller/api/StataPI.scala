package com.fijimf.deepfij.server.controller.api

import com.fijimf.deepfij.server.controller.Controller
import com.fijimf.deepfij.modelx.Team
import com.fijimf.deepfij.statx.Population
import java.text.SimpleDateFormat
import com.codahale.jerkson.Json
import java.util.Date
import com.fijimf.deepfij.view.{StatPanel, BasePage}


trait StatApi {
  this: Controller =>

  case class Stat(name: String, yyyymmddd: String, max:Double, min:Double, mean: Double, stdDev: Double, observations: List[Obs])

  case class Obs(name: String, rank: Double, value: Double)

  get("/stat/:key") {
    contentType = "text/html"
    BasePage(title = "Deep Fij Admin", content = Some(StatPanel(params("key")))).toHtml5()
  }
  get("/api/stat/:key") {
    contentType = "application/json"
    val s = std.statistic(params("key"))
    generateJSON(s.name, s.endDate, s.population(s.endDate))
  }

  get("/api/stat/:key/:date") {
    contentType = "application/json"
    val s = std.statistic(params("key"))
    val fmt = new SimpleDateFormat("yyyyMMdd")
    val d = fmt.parse(params("date"))
    generateJSON(s.name, d, s.population(d))
  }

  def generateJSON(name: String, date: Date, population: Population[Team]): String = {
    val fmt = new SimpleDateFormat("yyyyMMdd")
    val os: List[Obs] = population.topN(population.keys.size).map {
      case (team: Team, d: Double) => Obs(team.name, population.rank(team).getOrElse(0.0), d)
    }
    val data: Stat = Stat(name, fmt.format(date), population.max.getOrElse(0),population.min.getOrElse(0),population.mean.getOrElse(0), population.stdDev.getOrElse(0), os)
    Json.generate(data)
  }

}
