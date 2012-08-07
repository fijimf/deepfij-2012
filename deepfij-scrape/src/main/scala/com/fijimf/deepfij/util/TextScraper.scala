package com.fijimf.deepfij.util

import io.Source
import java.net.URL


trait TextScraper extends Scraper[List[String]] {
  def loadURL(url: String): List[String] = {
    try {
      Source.fromURL(new URL(url)).getLines().toList
    }
    catch {
      case t: Throwable => List.empty[String]
    }
  }

  def loadString(s: String): List[String] = {
    s.split("\n").toList
  }
}