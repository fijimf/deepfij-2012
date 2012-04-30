package com.fijimf.deepfij.server

import cc.spray.test.SprayTest
import org.specs2.mutable.Specification
import cc.spray.http.HttpHeaders.Accept
import cc.spray.http.{HttpResponse, HttpRequest}

class DeepFijServiceSpec extends Specification with SprayTest with DeepFijService {
  def activeScheduleKey() = "Key"

  def start() {}

  def shutdown() {}


}

