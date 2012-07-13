package com.fijimf.deepfij.server

import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.SimpleHash
import org.apache.shiro.util.{ByteSource, Factory}


object ShiroTester {
  def main(args: Array[String]) {
    println("Hello")

    val factory: Factory[SecurityManager] = new IniSecurityManagerFactory("classpath:shiro.ini")
    val securityManager: SecurityManager = factory.getInstance()

    SecurityUtils.setSecurityManager(securityManager)

    val currentUser: Subject = SecurityUtils.getSubject
    val hex: String = new SimpleHash("SHA-256", "mutombo", ByteSource.Util.bytes("Fridge Rulesfijimf@gmail.com"), 1024).toHex
    println(hex)

    if (!currentUser.isAuthenticated) {
      val token: UsernamePasswordToken = new UsernamePasswordToken("fijimf@gmail.com", "mutombo")
      token.setRememberMe(true)
      currentUser.login(token)

      currentUser.checkRole("ADMIN")
      println(currentUser.isAuthenticated)
    }
  }
}
