package com.fijimf.deepfij.server

import cc.spray._
import http.HttpCredentials
import http.HttpHeaders.Authorization
import cc.spray.AuthenticationRequiredRejection._
import cc.spray.AuthenticationFailedRejection._
import akka.dispatch.Future
import com.fijimf.deepfij.modelx.{UserDao, User}


trait UserAuthenticator[U] extends UserPassAuthenticator[User] {
  def apply(v1: Option[(String, String)]): Future[Option[User]] = null
}