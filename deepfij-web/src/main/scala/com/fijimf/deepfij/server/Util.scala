package com.fijimf.deepfij.server

import xml.{Elem, NodeSeq}


object Util {
  def html5Wrapper(xml: NodeSeq): String = {
    "<!DOCTYPE html>\n" + xml.toString
  }

  def ordinal(i: Int): String = {
    val suffixes = Array("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    (i % 100) match {
      case 11 => "11th"
      case 12 => "12th"
      case 13 => "13th"
      case _ => i + suffixes(i % 10)
    }
  }

  def createAccordian(accordianId: String, children: List[(String, NodeSeq, NodeSeq)], parent: Boolean = false): Elem = {
    val par = if (parent) Some(accordianId) else None
    <div class="accordion" id={accordianId}>
      {children.map(tup => createAccordianMember(tup._1, tup._2, tup._3, par))}
    </div>
  }


  def createAccordianMember(id: String, heading: NodeSeq, content: NodeSeq, parent: Option[String]): Elem = {
    <div class="accordion-group">
      <div class="accordion-heading">
        <h3>
          {if (parent.isDefined) {
          <a class="accordion-toggle" data-toggle="collapse" data-parent={"#" + parent.get} href={"#" + id}>
            {heading}
          </a>
        } else {
          <a class="accordion-toggle" data-toggle="collapse" href={"#" + id}>
            {heading}
          </a>
        }}
        </h3>
      </div>
      <div id={id} class="accordion-body collapse" style="height: 0px; ">
        <div class="accordion-inner">
          {content}
        </div>
      </div>
    </div>
  }


}