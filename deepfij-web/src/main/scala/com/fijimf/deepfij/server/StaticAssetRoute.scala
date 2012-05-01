package com.fijimf.deepfij.server

import cc.spray.Route
import cc.spray.directives.Remaining
import cc.spray.Directives

object StaticAssetRoute extends Directives {

  def apply(urlPath: String = "static", filePath: String = "/public/"): Route = path(urlPath / Remaining) {
    resource =>
      cache {
        {
          getFromResource(filePath + resource)
        }
      }
  }
}