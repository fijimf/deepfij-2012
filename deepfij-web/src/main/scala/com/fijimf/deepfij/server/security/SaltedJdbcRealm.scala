/*
 * Created by IntelliJ IDEA.
 * User: fijimf
 * Date: 5/15/12
 * Time: 3:52 AM
 */
package com.fijimf.deepfij.server.security

import org.apache.shiro.realm.jdbc.JdbcRealm

class SaltedJdbcRealm extends JdbcRealm {
  super.setSaltStyle(JdbcRealm.SaltStyle.EXTERNAL)

  override def getSaltForUser(username: String) = "Fridge Rules"+super.getSaltForUser(username)
}