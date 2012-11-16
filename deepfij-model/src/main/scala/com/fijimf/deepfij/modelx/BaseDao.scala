package com.fijimf.deepfij.modelx

import javax.persistence.EntityNotFoundException


abstract class BaseDao[T: ClassManifest, ID] extends Transactional {
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

  def saveAll(list: List[T]): List[T] = {
    val t = transactional {
      list.map(entityManager.merge(_))
    }
    entityManager.clear() //Flush 1st level cache
    t
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
