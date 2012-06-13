package com.fijimf.deepfij.view

import xml.{Node, NodeSeq}
import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils
import com.fijimf.deepfij.server.Util


trait Page {

  def subject: Subject = SecurityUtils.getSubject

  def isUserKnown = subject.isRemembered || subject.isAuthenticated

  def principal = subject.getPrincipal

  def title: String = ""

  def content: Option[NodeSeq]

  def scripts: List[String]

  case class Link(href: String, rel: String, typ: String)

  def links: List[Link]

  def navbar: Node = {
    val items: NodeSeq = List(navbarLeftItems, navbarSearch, navbarRightItems).flatten
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">DeepFij</a>{items}
        </div>
      </div>
    </div>
  }


  def navbarSearch: Option[Node] = None

  def navbarLeftItems: Option[Node] = None

  def navbarRightItems: Option[Node] = None


  def toNodeSeq(flash: Map[String, String] = Map.empty): NodeSeq = {
    <html lang="en">
      <head>
          <meta charset="utf-8"/>
        <title>
          {title}
        </title>{scripts.map(s => <script src={s}></script>)}{links.map(l => <link href={l.href} rel={l.rel} type={l.typ}/>)}

      </head>

      <body style="padding-top: 50px;">
        {navbar}<div class="container">
        <div class="row">
          <div class="span12">
            <p class="epigram"></p>
          </div>
        </div>{if (content.isDefined) content.get}<div class="row">
        </div>
        <footer class="footer">
          <p>
            {"Copyright 2007-2012 Jim Frohnhofer"}
          </p>
        </footer>
      </div>
      </body>
    </html>
  }


  def toString(flash: Map[String, String] = Map.empty) = {
    toNodeSeq(flash).toString()
  }

  def toHtml5(flash: Map[String, String] = Map.empty) = {
    Util.html5Wrapper(toNodeSeq(flash))
  }


}
