package com.fijimf.deepfij.server

import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.util.Factory
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.authc.UsernamePasswordToken


object ShiroTester {
  def main(args: Array[String]) {
    println("Hello")

    val factory: Factory[SecurityManager] = new IniSecurityManagerFactory("classpath:shiro.ini")
    val securityManager: SecurityManager = factory.getInstance();

    SecurityUtils.setSecurityManager(securityManager)

    val currentUser:Subject = SecurityUtils.getSubject()

    println(currentUser)

    if ( !currentUser.isAuthenticated() ) {
      //collect user principals and credentials in a gui specific manner
      //such as username/password html form, X509 certificate, OpenID, etc.
      //We'll use the username/password example here since it is the most common.
      val token:UsernamePasswordToken = new UsernamePasswordToken("fijimf", "mutombo");

      //this is all you have to do to support 'remember me' (no config - built in!):
      token.setRememberMe(true);

      currentUser.login(token);
    }
  }
}
