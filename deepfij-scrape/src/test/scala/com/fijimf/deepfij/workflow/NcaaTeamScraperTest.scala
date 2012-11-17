package com.fijimf.deepfij.workflow

import io.Source
import com.fijimf.deepfij.util.Util._
import xml.Node
import com.fijimf.deepfij.util.HttpScraper


object ZZZZZ {
  def main(args: Array[String]) {
    val stream = classOf[Deepfij].getClassLoader.getResourceAsStream("html/ncaa/schools_b.html")
    val scr = new HttpScraper {}.map(p => {
      nodesByTagClass(p, "span", "field-content").flatMap(n => (n \ "a").map(m => {
        ((m \ "@href").text.split("/").last -> m.text)
      }))
    })
    val xx = scr.loadString(Source.fromInputStream(stream).getLines().mkString("\n"))
    println(xx)

    val yy = classOf[Deepfij].getClassLoader.getResourceAsStream("html/ncaa/schools_b.html")


  }
}

class NewNcaa {
  val alphaTeamScraper = new HttpScraper {}.map(p => {
    nodesByTagClass(p, "span", "field-content").flatMap(n => (n \ "a").map(m => {
      ((m \ "@href").text.split("/").last -> m.text)
    }))
  })

  val shortNameScraper = new HttpScraper {}.map(p =>
    (p \\ "a").filter((node: Node) => (node \ "@href").text.startsWith("/schools/")).map((node: Node) => {
      ((node \ "@href").text.replace("/schools/", "") -> node.text)
    })

  )
}

