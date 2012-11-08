package com.fijimf.deepfij.util

import org.apache.log4j.Logger

trait Logging {
  lazy val log = Logger.getLogger(this.getClass)

}
