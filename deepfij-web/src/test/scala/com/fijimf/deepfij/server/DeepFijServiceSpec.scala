package com.fijimf.deepfij.server

import cc.spray.test.SprayTest
import org.specs2.mutable.Specification
import cc.spray.http.HttpHeaders.Accept
import cc.spray.http.{HttpResponse, HttpRequest}

class DeepFijServiceSpec extends Specification with SprayTest with DeepFijService {
  "The Calculator service" should {
      "calculate correctly" in {
        "for 'add's" in {
          testService(HttpRequest(GET, "/add/35/7.2")) {
            calculatorService
          }.response.content.as[String] mustEqual Right("<double>42.2</double>")
        }
        "for 'substract's" in {
          testService(HttpRequest(GET, "/substract/35.1/7", headers = List(`Accept`(`text/plain`)))) {
            calculatorService
          }.response.content.as[String] mustEqual Right("28.1")
        }
        "for 'multiply's" in {
          testService(HttpRequest(GET, "/multiply/35/7")) {
            calculatorService
          }.response.content.as[String] mustEqual Right("<double>245.0</double>")
        }
        "for 'divide's" in {
          testService(HttpRequest(GET, "/divide/35/7")) {
            calculatorService
          }.response.content.as[String] mustEqual Right("<double>5.0</double>")
        }
      }
      "use the given onDivZero parameter value on division by zero" in {
        testService(HttpRequest(GET, "/divide/12/0?onDivZero=nah!")) {
          calculatorService
        }.response.content.as[String] mustEqual Right("nah!")
      }
      "return a MethodNotAllowed error for POST requests to '/substract/123/234'" in {
        testService(HttpRequest(POST, "/substract/123/234")) {
          calculatorService
        }.response mustEqual HttpResponse(MethodNotAllowed, "HTTP method not allowed, supported methods: GET")
      }
      "leave GET requests to other paths unhandled" in {
        testService(HttpRequest(GET, "/multiply/x/22")) {
          calculatorService
        }.handled must beFalse
      }

}

