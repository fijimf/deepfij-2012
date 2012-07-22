package com.fijimf.deepfij.matchers

import org.scalatest.matchers.{MatchResult, BeMatcher}
import xml.{Elem, XML}
import scala.util.control.Exception._
import java.io.StringReader

trait CustomMatchers {

  class ValidHtml5Matcher extends BeMatcher[String] {
    def apply(left: String) = {
      val (doctype, x) = left.splitAt(left.indexOf("<html"))
      if (doctype.trim == "<!DOCTYPE html>") {
        catching(classOf[Exception]).either {
          XML.load(new StringReader(x.trim))
        } match {
          case Right(xml) => MatchResult(matches = true, failureMessage = left.toString + " was not valid html5", negatedFailureMessage = left.toString + " was valid html5")
          case Left(e) => MatchResult(matches = false, failureMessage = left.toString + " was not valid html5\n"+e.getMessage, negatedFailureMessage = left.toString + " was valid html5")
        }
      } else {
        MatchResult(matches = false, failureMessage = "Incorrect DOCTYPE", negatedFailureMessage = "Correct DOCTYPE")
      }
    }

    def checkXml(xml: Elem): MatchResult = {
      if ((xml \ "head").isEmpty)
        MatchResult(matches = false, failureMessage = "Missing <head>", negatedFailureMessage = "Not Missing <head>")
      else if ((xml \ "body").isEmpty)
        MatchResult(matches = false, failureMessage = "Missing <head>", negatedFailureMessage = "Not Missing <head>")
      else
        MatchResult(matches = true, failureMessage = "Invalid HTML", negatedFailureMessage = "Valid HTML")
    }
  }

  val validHtml5 = new ValidHtml5Matcher

}

object CustomMatchers extends CustomMatchers

