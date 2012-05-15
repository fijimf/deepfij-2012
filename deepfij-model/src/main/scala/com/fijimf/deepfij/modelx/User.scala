package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field

@Entity
@Table(name = "user")
class User(
            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(Column@field)(name = "email", nullable = false)
            val email: String = "",

            @(Column@field)(name = "password", nullable = false)
            val password: String = "",

            @(ManyToMany@field)
            @(JoinTable@field)(
              name = "user_role",
              joinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")),
              inverseJoinColumns = Array(new JoinColumn(name = "user_id", referencedColumnName = "id"))
            )
            val roles: java.util.Set[Role] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Role]],

            @(Column@field)(name = "updatedAt", nullable = false)
            val updatedAt: Date = new Date
            ) {
  def this() = {
    this(0L, "", "",java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Role]], new Date())
  }
}

class UserDao extends BaseDao[User, Long] {

}
