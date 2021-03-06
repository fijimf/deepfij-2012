package com.fijimf.deepfij.modelx

import annotation.target.field
import javax.persistence._
import com.fijimf.deepfij.statx.StatInfo
import scala.collection.JavaConversions._


@Entity
@Table(name = "metaStat",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("modelKey", "keyName")),
    new UniqueConstraint(columnNames = Array("modelName", "name")))
)
class MetaStat(@(Id@field)
               @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
               @(Column@field)(name = "id")
               val id: Long = 0L,

               @(Column@field)(name = "modelKey")
               val modelKey: String = "",

               @(Column@field)(name = "modelName")
               val modelName: String = "",

               @(Column@field)(name = "keyName")
               val statKey: String = "",

               @(Column@field)(name = "name")
               val name: String = "",

               @(Column@field)(name = "format", unique = false)
               val format: String = "%f",

               @(Column@field)(name = "higherIsBetter")
               val higherIsBetter: Boolean = true,

               @(OneToMany@field)(mappedBy = "metaStat", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[TeamStat])
               val values: java.util.Set[TeamStat] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[TeamStat]],

               @(OneToMany@field)(mappedBy = "metaStat", cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, targetEntity = classOf[StatParameter])
               val parameters: java.util.Set[StatParameter] = java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[StatParameter]]) extends StatInfo {
  def this() = {
    this(0L, "", "", "", "", "%f", true, java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[TeamStat]], java.util.Collections.EMPTY_SET.asInstanceOf[java.util.Set[StatParameter]])
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

  def findByModelKey(modelKey: String): Option[MetaStat] = {
    try {
      val m: MetaStat = entityManager.createQuery("SELECT m FROM MetaStat m WHERE m.modelKey=:modelKey").setParameter("modelKey", modelKey).getSingleResult.asInstanceOf[MetaStat]
      Some(m)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }

  def findByModelKey(modelKey: String, statKey: String): Option[MetaStat] = {
    try {
      val m: MetaStat = entityManager.createQuery("SELECT m FROM MetaStat m WHERE m.modelKey=:modelKey AND  m.statKey=:statKey").setParameter("modelKey", modelKey).setParameter("statKey", statKey).getSingleResult.asInstanceOf[MetaStat]
      Some(m)
    }
    catch {
      case x: NoResultException => None
      case x: NonUniqueResultException => None
    }
  }

  def findAll(): List[MetaStat] = entityManager.createQuery("SELECT m FROM MetaStat m").getResultList.toList.asInstanceOf[List[MetaStat]]
}

