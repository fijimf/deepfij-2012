package com.fijimf.deepfij.view.user

import org.apache.shiro.SecurityUtils

object UserMap {
  def apply(): Map[String, Any] = {
    val subject = SecurityUtils.getSubject
    val remembered = subject.isRemembered
    val authenticated = subject.isAuthenticated
    if (remembered || authenticated) {
      Map(
        "user" -> Map(
          "name" -> subject.getPrincipal.toString,
          "isAuthenticated" -> authenticated,
          "isRemembered" -> remembered
        )
      )
    } else {
      Map.empty[String, Any]
    }
  }
}
