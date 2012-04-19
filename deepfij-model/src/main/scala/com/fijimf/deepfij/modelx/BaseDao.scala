package com.fijimf.deepfij.modelx

import javax.persistence.{EntityNotFoundException, Persistence, EntityManager}


abstract class BaseDao[T: ClassManifest, ID] extends Transactional {
  val entityManager = PersistenceSource.entityManager

  def findBy(id: ID): Option[T] = {
    transactional {
      try {
        Option(entityManager.find(classManifest[T].erasure, id).asInstanceOf[T])
      }
      catch {
        case x:EntityNotFoundException => None
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

  def delete(id: ID) {
    transactional {
      Option(entityManager.find(classManifest[T].erasure, id)).map(x => entityManager.remove(x))
    }
  }

}
