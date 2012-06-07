package com.fijimf.deepfij.server.filter.api

import com.fijimf.deepfij.server.filter.Controller
import com.fijimf.deepfij.modelx.{Team, MetaStat, MetaStatDao}
import com.fijimf.deepfij.statx.{Population, Statistic}


trait StatController {
  this: Controller =>

  get("/api/stat/:key") {
    val statistic: Statistic[Team] = std.statistic(params("key"))
    val population: Population[Team] = statistic.population(statistic.endDate)


  }

  get("/api/stat/:key/:date") {

  }


}
