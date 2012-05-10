package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field

@Entity
@Table(name = "permission")
class Permission(

            @(Id@field)
            @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
            @(Column@field)(name = "id", nullable = false)
            val id: Long = 0L,

            @(Column@field)(name = "permission", nullable = false)
            val name: String = "",

            @(ManyToOne@field)
            val role: Role = null,

            @(Column@field)(name = "updatedAt", nullable = false)
            val updatedAt: Date = new Date
            ) {
  def this() = {
    this(0L, "",null, new Date())
  }
}

class PermissionDao extends BaseDao[Permission, Long] {


}
