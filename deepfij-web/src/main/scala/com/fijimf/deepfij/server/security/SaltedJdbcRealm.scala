/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/15/12
 * Time: 3:52 AM
 */
package com.fijimf.deepfij.server.security

import org.apache.shiro.realm.jdbc.JdbcRealm
import org.apache.shiro.crypto.hash.SimpleHash
import org.apache.shiro.util.ByteSource

class SaltedJdbcRealm extends JdbcRealm {
  super.setSaltStyle(JdbcRealm.SaltStyle.EXTERNAL)

  override def getSaltForUser(username: String) = "Fridge Rules" + super.getSaltForUser(username)

  def encrypt(user: String, plaintext: String) = {
    new SimpleHash("SHA-256", plaintext, ByteSource.Util.bytes(getSaltForUser(user)), 1024).toHex
  }
}