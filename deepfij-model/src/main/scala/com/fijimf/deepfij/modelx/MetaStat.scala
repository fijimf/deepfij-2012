package com.fijimf.deepfij.modelx

import annotation.target.field
import javax.persistence._
import com.fijimf.deepfij.statx.StatInfo


@Entity
@Table(name = "metaStat")
class MetaStat(@(Id@field)
               @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
               @(Column@field)(name = "id")
               val id: Long = 0L,

               @(Column@field)(name = "keyName", unique = true)
               val statKey: String = "",

               @(Column@field)(name = "name", unique = true)
               val name: String = "",

               @(Column@field)(name = "format", unique = false)
               val format: String = "%f",

               @(Column@field)(name = "higherIsBetter")
               val higherIsBetter: Boolean = true,

               @(OneToMany@field)(mappedBy = "metaStat", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[TeamStat])
               val values: java.util.Set[TeamStat] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[TeamStat]]) extends StatInfo {
  def this() = {
    this(0L, "", "", "%f", true, java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[TeamStat]])
  }
}

class MetaStatDao extends BaseDao[MetaStat, Long] {

}

