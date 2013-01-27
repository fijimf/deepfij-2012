package com.fijimf.deepfij.modelx

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RoleTestSuite extends DaoTestSuite {
  val dao: RoleDao = new RoleDao

  val pdao: PermissionDao = new PermissionDao


  test("Find") {
    assert(dao.findBy(999).isEmpty)
    val r1 = dao.save(new Role(name = "Admin"))
    val role: Role = dao.findBy(r1.id).get
    assert(role.name == "Admin" && role.userList.isEmpty && role.permissionList.isEmpty)

    val permission: Permission = new Permission(permission = "Update", roles = new java.util.HashSet[Role])
    val p1 = pdao.save(permission)
    role.permissions.add(p1)
    dao.save(role)

    assert(pdao.findBy(p1.id).get.roleList.size == 1)

    val r2 = dao.findBy(r1.id).get
    assert(r2.permissionList.size == 1)
    assert(r2.permissionList.head.permission == "Update")

  }

  test("Create conditions") {
    try {
      new Role(name = "")
    } catch {
      case _: Throwable => //OK
    }

    try {
      new Role(name = null)
    } catch {
      case _: Throwable => //OK
    }
  }

  test("Uniqueness") {
    dao.save(new Role(name = "Admin"))
    try {
      dao.save(new Role(name = "Admin"))
    } catch {
      case _: Throwable => //OK
    }

  }
}