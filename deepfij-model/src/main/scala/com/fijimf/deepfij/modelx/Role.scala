package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field

@Entity
@Table(name = "role")
class Role(

            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(Column@field)(name = "name", nullable = false)
            val name: String = "User",

            @(ManyToMany@field)
            @(JoinTable@field)(
              name = "user_role",
              joinColumns = Array(new JoinColumn(name = "user_id", referencedColumnName = "id")),
              inverseJoinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id"))
            )
            val users: java.util.Set[User] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[User]],

            @(Column@field)(name = "updatedAt", nullable = false)
            val updatedAt: Date = new Date
            ) {
  def this() = {
    this(0L, "", java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[User]], new Date())
  }
}

class RoleDao extends BaseDao[Role, Long] {


}
