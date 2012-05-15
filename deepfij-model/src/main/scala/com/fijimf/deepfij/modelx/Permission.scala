package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field
import scala.collection.JavaConversions._

@Entity
@Table(name = "permission")
class Permission(

            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(Column@field)(name = "permission", nullable = false, unique=true)
            val name: String = "",

            @(ManyToMany@field)
            @(JoinTable@field)(
              name = "role_permission",
              joinColumns = Array(new JoinColumn(name = "permission_id", referencedColumnName = "id")),
              inverseJoinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id"))
            )
            val roles: java.util.Set[Role] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Role]],

            @(Column@field)(name = "updatedAt", nullable = false)
            val updatedAt: Date = new Date
            ) {
  def this() = {
    this(0L, "",java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[Role]], new Date())
  }

  @transient lazy val roleList = roles.toList
}

class PermissionDao extends BaseDao[Permission, Long] {


}
