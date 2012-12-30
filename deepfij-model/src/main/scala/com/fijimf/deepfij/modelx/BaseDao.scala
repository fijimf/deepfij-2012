package com.fijimf.deepfij.modelx

import javax.persistence.{EntityManager, EntityNotFoundException}
import org.apache.log4j.Logger


abstract class BaseDao[T: ClassManifest, ID] extends Transactional {
  val log = Logger.getLogger(this.getClass)

  def entityManager = PersistenceSource.entityManager

  def findBy(id: ID): Option[T] = {
    transactional {
      try {
        Option(entityManager.find(classManifest[T].erasure, id).asInstanceOf[T])
      }
      catch {
        case x: EntityNotFoundException => None
      }
    }
  }

  def save(data: T): T = {
    val t = transactional {
      entityManager.merge(data)
    }
    entityManager.clear() //Flush 1st level cache
    t
  }

  def saveAll(list: List[T]): Unit = {
    val em: EntityManager = entityManager
    log.info("Starting save of %d objects".format(list.size))
    transactional {
      list.foreach(t => {
        em.persist(t)
      })
    }
    em.clear() //Flush 1st level cache
    log.info("Save complete")
  }

  def delete(id: ID) {
    transactional {
      Option(entityManager.find(classManifest[T].erasure, id)).map(x => entityManager.remove(x))
    }
  }

  def deleteObjects(lst: List[T]) {
    transactional {
      lst.foreach(t => entityManager.remove(t))
    }
    entityManager.clear() //Flush 1st level cache
  }


}
