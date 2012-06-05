package com.fijimf.deepfij.view

import xml.NodeSeq
import collection.immutable.Map
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject

object BasePage {
  def subject: Subject = SecurityUtils.getSubject

  def apply(title: String = "", content: Option[NodeSeq], flash: Map[String, String] = Map.empty): NodeSeq = {
    <html lang="en">
      <head>
          <meta charset="utf-8"/>
        <title>
          {title}
        </title>

        <script src="/scripts/jquery-1.7.1.min.js"></script>
        <script src="/scripts/bootstrap.min.js"></script>
        <script src="/scripts/quoteloader.js"></script>

          <link href="http://fonts.googleapis.com/css?family=Lobster" rel="stylesheet" type="text/css"/>
          <link href="/style/bootstrap.css" rel="stylesheet" type="text/css"/>
          <link href="/style/deepfij.css" rel="stylesheet" type="text/css"/>
          <link href="/style/fontawesome.css" rel="stylesheet" type="text/css"/>
          <link rel="shortcut icon" type="image/x-icon" href="assets/ico/favicon.ico"/>
      </head>

      <body style="padding-top: 50px;">

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
              </form>
              {userPullRight}
            </div>
          </div>
        </div>

        <div class="container">
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

  def userPullRight = {
    <ul class="nav pull-right">
      {if (userIsKnown) {
      userItems
    } else {
      noUserItems
    }}
    </ul>
  }


  def userIsKnown: Boolean = {
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
