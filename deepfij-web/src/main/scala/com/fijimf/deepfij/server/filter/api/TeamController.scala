package com.fijimf.deepfij.server.filter.api

import com.fijimf.deepfij.server.filter.Controller


trait TeamController {
  this: Controller =>

  get("/api/team/:key") {

  }

  get("/api/team/:key/series/:stat") {

  }
}
