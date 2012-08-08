package com.fijimf.deepfij.util

import xml.Node
import org.xml.sax.InputSource
import java.io.{Reader, StringReader}
import scala.util.control.Exception._
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import xml.parsing.NoBindingFactoryAdapter


object HtmlHelper {
  def loadHtml(url: String): Option[Node] = {
    catching(classOf[Exception]).opt {
      val adapter = new NoBindingFactoryAdapter()
      adapter.loadXML(new InputSource(url), new SAXFactoryImpl().newSAXParser())
    }
  }

  def loadHtmlFromReader(r: Reader): Option[Node] = {
    catching(classOf[Exception]).opt {
      val adapter = new NoBindingFactoryAdapter()
      adapter.loadXML(new InputSource(r), new SAXFactoryImpl().newSAXParser())
    }
  }

  def loadHtmlFromString(s: String): Option[Node] = {
    loadHtmlFromReader(new StringReader(s))
  }
}

trait HttpScraper extends Scraper[Node] {

  def loadURL(url: String): Node = {
    loadPage(new org.xml.sax.InputSource(url))
  }

  def loadString(s: String): Node = {
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
      case t: Throwable => <exception>
        {t.getMessage}
      </exception>
    }
  }
}
