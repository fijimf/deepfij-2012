package com.fijimf.deepfij.modelx

import javax.persistence._
import java.util.Date
import annotation.target.field

@Entity
@Table(name = "quote")
class Quote(

                  @(Id@field)
                  @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
                  @(Column@field)(name = "id", nullable = false)
                  val id: Long = 0L,

                  @(Column@field)(name = "quote", nullable = false)
                  var quote: String = "x",

                  @(Column@field)(name = "source", nullable = true)
                  var source: String = "",

                  @(Column@field)(name = "url", nullable = true)
                  var url: String = "",

                  @(Column@field)(name = "updatedAt", nullable = false)
                  var updatedAt: Date = new Date
                  ) {
  def this() = {
    this(0L, "", "", "", new Date())
  }
}

class QuoteDao extends BaseDao[Quote, Long] {

  import scala.collection.JavaConversions._

  def findAll(): List[Quote] = entityManager.createQuery("SELECT q FROM Quote q").getResultList.toList.asInstanceOf[List[Quote]]

  def random(): Option[Quote] = {
    entityManager.createQuery("SELECT q FROM Quote q ORDER BY RAND()").setMaxResults(1).getResultList.toList.asInstanceOf[List[Quote]].headOption
  }
}

