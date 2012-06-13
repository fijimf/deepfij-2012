package com.fijimf.deepfij.view

import collection.immutable.Map
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import com.fijimf.deepfij.server.Util._
import xml.{Node, NodeSeq}

case class BasePage(override val title: String, override val content: Option[NodeSeq]) extends Page {

  override val scripts = List(
    "/scripts/jquery-1.7.1.min.js",
    "/scripts/bootstrap.min.js",
    "/scripts/quoteloader.js",
    "/scripts/d3.v2.min.js"
  )

  override val links = List(
    Link("http://fonts.googleapis.com/css?family=Lobster", "stylesheet", "text/css"),
    Link("/style/bootstrap.css", "stylesheet", "text/css"),
    Link("/style/deepfij.css", "stylesheet", "text/css"),
    Link("assets/ico/favicon.ico", "shortcut icon", "image/x-icon")
  )

  override def navbarSearch: Option[Node] = {
    Some(<form action="/search" method="get" class="navbar-search pull-left">
        <input name="q" type="text" class="search-query" placeholder="Team, Conference, Statistic..."/>
    </form>)
  }

  override def navbarLeftItems: Option[Node] = {
    Some(<ul class="nav">
      <li class="active">
        <a href="/">Home</a>
      </li>{if (isUserKnown) {
        <li>
          <a href="/admin">Admin</a>
        </li>
      }}<li>
        <a href="/about">About</a>
      </li>
    </ul>)
  }

  override def navbarRightItems: Option[Node] = {
    Some(<ul class="nav pull-right">
      {if (isUserKnown) {
        userItems
      } else {
        noUserItems
      }}
    </ul>)
  }

  def userItems: NodeSeq = {
    <li>
      <a href="/user">
        {principal.toString}
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



