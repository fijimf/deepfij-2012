package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UserTestSuite extends DaoTestSuite {
  val dao: UserDao = new UserDao
  val rdao: RoleDao = new RoleDao

  test("Find") {
    assert(dao.findBy(999).isEmpty)
    val u1 = dao.save(new User(email = "a@a.com", password = "xxxxx"))
    val user: User = dao.findBy(u1.id).get
    assert(user.email == "a@a.com")
    assert(user.password == "xxxxx")
    assert(user.roleList.isEmpty)

    val r: Role = new Role(name = "Admin", permissions = new java.util.HashSet[Permission])
    val r1 = rdao.save(r)
    user.roles.add(r1)
    dao.save(user)

    assert(rdao.findBy(r1.id).get.userList.size == 1)

    val u2 = dao.findBy(u1.id).get
    assert(u2.roleList.size == 1)
    assert(u2.roleList.head.name == "Admin")

  }

  test("Create conditions") {
    try {
      new User(email = "", password = "xxxx")
    } catch {
      case _: IllegalArgumentException => //OK
    }

    try {
      new User(email = "a@a.com", password = "")
    } catch {
      case _: IllegalArgumentException => //OK
    }

    try {
      new User(email = null, password = "xxxx")
    } catch {
      case _: IllegalArgumentException => //OK
    }

    try {
      new User(email = "a@a.com", password = null)
    } catch {
      case _: IllegalArgumentException => //OK
    }

  }

  test("Uniqueness") {
    dao.save(new User(email = "a@a.com", password = "xxxx"))
    assert(try {
      dao.save(new User(email = "a@a.com", password = "xxxx"))
      false
    } catch {
      case _: RuntimeException => true
    })

  }
}