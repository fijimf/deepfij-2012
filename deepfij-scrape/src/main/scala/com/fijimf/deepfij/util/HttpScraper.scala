package com.fijimf.deepfij.util

import xml.Node
import io.Source
import java.net.URL
import org.xml.sax.InputSource
import java.io.{Reader, StringReader}


trait HttpScraper {

  def loadTextPage(url: String): List[String] = {
    try {
      Source.fromURL(new URL(url)).getLines().toList
    }
    catch {
      case t: Throwable => List.empty[String]
    }
  }

  def loadPage(url: String): Node = {
    loadPage(new org.xml.sax.InputSource(url))
  }

  def loadString(s:String):Node = {
    loadReader(new StringReader(s))
  }


  def loadReader(reader: Reader): Node = {
    loadPage(new InputSource(reader))
  }

  def loadPage(source: InputSource): Node = {
    try {
      val parserFactory = new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
      val parser = parserFactory.newSAXParser()
      val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
      adapter.loadXML(source, parser)
    }
    catch {
      case t: Throwable => <exception>{t.getMessage}</exception>
    }
  }
}