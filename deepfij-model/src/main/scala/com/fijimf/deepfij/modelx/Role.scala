package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._

@Entity
@Table(name = "role")
class Role(

            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(Column@field)(name = "name", nullable = false, unique = true)
            val name: String = "",

            @(ManyToMany@field)
            @(JoinTable@field)(
              name = "user_role",
              joinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")),
              inverseJoinColumns = Array(new JoinColumn(name = "user_id", referencedColumnName = "id"))
            )
            val users: java.util.Set[User] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[User]],

            @(ManyToMany@field)
            @(JoinTable@field)(
              name = "role_permission",
              joinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")),
              inverseJoinColumns = Array(new JoinColumn(name = "permission_id", referencedColumnName = "id"))
            )
            val permissions: java.util.Set[Permission] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Permission]],

            @(Column@field)(name = "updatedAt", nullable = false)
            var updatedAt: Date = new Date
            ) {
  def this() = {
    this(0L, "", java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[User]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Permission]], new Date())
  }

  @transient lazy val permissionList = permissions.toList
  @transient lazy val userList = users.toList
}

class RoleDao extends BaseDao[Role, Long] {


}
