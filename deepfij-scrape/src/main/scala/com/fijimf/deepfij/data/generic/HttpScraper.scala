package com.fijimf.deepfij.data.generic

import xml.Node
import io.Source
import java.net.URL


trait HttpScraper {
  
  def loadTextPage(url:String):List[String] = {
    Source.fromURL(new URL(url)).getLines().toList
  }

  def loadPage(url: String): Node = {
    val parserFactory = new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
    val parser = parserFactory.newSAXParser()
    val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
    val source = new org.xml.sax.InputSource(url)
    adapter.loadXML(source, parser)
  }
}