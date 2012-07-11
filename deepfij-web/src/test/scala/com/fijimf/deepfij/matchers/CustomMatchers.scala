package com.fijimf.deepfij.matchers

import org.scalatest.matchers.{MatchResult, BeMatcher}
import xml.XML
import scala.util.control.Exception._
import java.io.StringReader

trait CustomMatchers {

  class ValidHtml5Matcher extends BeMatcher[String] {
    def apply(left: String) = {
      val (doctype, xml) = left.splitAt(left.indexOf("<html"))
      if (doctype.trim == "<!DOCTYPE html>") {
        catching(classOf[Exception]).opt {
          XML.load(new StringReader(xml.trim))
        } match {
          case Some(x) => {
            if ((x \ "html" \ "head").isEmpty || (x \ "html" \ "body").isEmpty) {
              MatchResult(false, left.toString + " was not valid html5", left.toString + " was valid html5")
            } else {
              MatchResult(true, left.toString + " was not valid html5", left.toString + " was valid html5")
            }
          }
          case None => MatchResult(false, left.toString + " was not valid html5", left.toString + " was valid html5")
        }
      } else {
        MatchResult(false, "Incorrect DOCTYPE", "Correct DOCTYPE")
      }
    }
  }

  val validHtml5 = new ValidHtml5Matcher

}

object CustomMatchers extends CustomMatchers


