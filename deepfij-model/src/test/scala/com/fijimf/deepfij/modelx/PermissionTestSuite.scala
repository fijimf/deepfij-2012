package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PermissionTestSuite extends DaoTestSuite {
  val dao: PermissionDao = new PermissionDao

  test("Find") {

    assert(dao.findBy(999).isEmpty)

    val r = dao.save(new Permission(permission = "Update"))
    assert(dao.findBy(r.id).get.permission == "Update")
    assert(dao.findBy(r.id).get.roleList.isEmpty)
  }

  test("Create conditions") {
    try {
      new Permission(permission = "")
    } catch {
      case _: IllegalArgumentException => //OK
    }

    try {
      new Permission(permission = null)
    } catch {
      case _: IllegalArgumentException => //OK
    }
  }

  test("Uniqueness") {
    dao.save(new Permission(permission = "Admin"))
    assert(try {
      dao.save(new Permission(permission = "Admin"))
      false
    } catch {
      case e: RuntimeException => true
    })
  }

}