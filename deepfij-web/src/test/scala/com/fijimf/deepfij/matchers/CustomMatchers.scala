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
        catching(classOf[Exception]).opt {
          XML.load(new StringReader(x.trim))
        } match {
          case Some(xml) => MatchResult(true, left.toString + " was not valid html5", left.toString + " was valid html5")
          case None => MatchResult(false, left.toString + " was not valid html5", left.toString + " was valid html5")
        }
      } else {
        MatchResult(false, "Incorrect DOCTYPE", "Correct DOCTYPE")
      }
    }

    def checkXml(xml:Elem):MatchResult ={
      if ((xml\"head").isEmpty)
        MatchResult(false, "Missing <head>","Not Missing <head>")
      else if ((xml\"body").isEmpty)
        MatchResult(false, "Missing <head>","Not Missing <head>")
      else
        MatchResult(true, "Invalid HTML","Valid HTML")
    }
  }

  val validHtml5 = new ValidHtml5Matcher

}

object CustomMatchers extends CustomMatchers


