package com.fijimf.deepfij.util

import xml.Node
import io.Source
import java.net.URL
import org.xml.sax.InputSource
import java.io.StringReader

trait Loader[T] {
  def loadFromUrl(u: String): T

  def loadFromString(s: String): T
}

trait TextLoader extends Loader[List[String]] {
  def loadFromUrl(u: String) = Source.fromURL(new URL(u)).getLines().toList

  def loadFromString(s: String) = s.split("\n").toList
}

trait HtmlLoader extends Loader[Node] {
  def loadFromUrl(u: String) = loadPage(new org.xml.sax.InputSource(u))

  def loadFromString(s: String) = loadPage(new InputSource(new StringReader(s)))

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
