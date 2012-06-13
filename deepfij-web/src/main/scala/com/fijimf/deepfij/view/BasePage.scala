package com.fijimf.deepfij.view

import collection.immutable.Map
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import com.fijimf.deepfij.server.Util._
import xml.{Node, NodeSeq}

case class BasePage(override val title: String, override val content: Option[NodeSeq]) extends Page {
  def subject: Subject = SecurityUtils.getSubject

  override val scripts = List(
    "/scripts/jquery-1.7.1.min.js",
    "/scripts/bootstrap.min.js",
    "/scripts/quoteloader.js",
    "/scripts/d3.v2.min.js"
  )

  override val links = List(
    Link("http://fonts.googleapis.com/css?family=Lobster", "stylesheet", "text/css"),
    Link("/style/bootstrap.css", "stylesheet", "text/css"),
    Link("/style/deepfij.css", "shortcut icon", "text/css"),
    Link("assets/ico/favicon.ico", "stylesheet", "image/x-icon")
  )

  override def navbar: Node = {
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">DeepFij</a>
          <ul class="nav">
            <li class="active">
              <a href="/">Home</a>
            </li>{if (userIsKnown) {
            <li>
              <a href="/admin">Admin</a>
            </li>
          }}<li>
            <a href="/about">About</a>
          </li>
          </ul>
          <form action="/search" method="get" class="navbar-search pull-left">
              <input name="q" type="text" class="search-query" placeholder="Team, Conference, Statistic..."/>
          </form>{userPullRight}
        </div>
      </div>
    </div>
  }

  def userPullRight() = {
    <ul class="nav pull-right">
      {if (userIsKnown()) {
      userItems
    } else {
      noUserItems
    }}
    </ul>
  }

  def userIsKnown(): Boolean = {
    subject.isRemembered || subject.isAuthenticated
  }

  def userItems: NodeSeq = {
    <li>
      <a href="/user">
        {subject.getPrincipal.toString}
      </a>
    </li>
      <li>
        <a href="/logout">Logout</a>
      </li>
  }

  def noUserItems: NodeSeq = {
    <li>
      <a href="/login">Login</a>
    </li>
      <li>
        <a href="/register">Register</a>
      </li>
  }
}


trait Page {

  def title: String = ""

  def content: Option[NodeSeq]

  def scripts: List[String]

  case class Link(href: String, rel: String, typ: String)

  def links: List[Link]

  def navbar: Node

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
    html5Wrapper(toNodeSeq(flash))
  }


}
