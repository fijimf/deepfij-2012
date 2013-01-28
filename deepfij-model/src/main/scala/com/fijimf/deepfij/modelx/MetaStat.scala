package com.fijimf.deepfij.modelx

import annotation.target.field
import javax.persistence._
import com.fijimf.deepfij.statx.StatInfo
import scala.collection.JavaConversions._


@Entity
@Table(name = "metaStat")
class MetaStat(@(Id@field)
               @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
               @(Column@field)(name = "id")
               val id: Long = 0L,

               @(Column@field)(name = "modelKeyName", unique = true)
               val modelKey: String = "",

               @(Column@field)(name = "modelName", unique = true)
               val modelName: String = "",

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
  def findByStatKey(statKey: String): Option[MetaStat] = {
    try {
      val m: MetaStat = entityManager.createQuery("SELECT m FROM MetaStat m WHERE m.statKey=:statKey").setParameter("statKey", statKey).getSingleResult.asInstanceOf[MetaStat]
      Some(m)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }
  def findByModelKey(statKey: String): Option[MetaStat] = {
    try {
      val m: MetaStat = entityManager.createQuery("SELECT m FROM MetaStat m WHERE m.modelKey=:modelKey").setParameter("modelKey", modelKey).getSingleResult.asInstanceOf[MetaStat]
      Some(m)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }

  def findAll(): List[MetaStat] = entityManager.createQuery("SELECT m FROM MetaStat m").getResultList.toList.asInstanceOf[List[MetaStat]]
}

