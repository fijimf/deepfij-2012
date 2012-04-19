package com.fijimf.deepfij.modelx

import javax.persistence.EntityManager

trait Transactional {
  def entityManager: EntityManager

  def transactional[T](block: => T) = {
    val hasTransaction = entityManager.getTransaction.isActive
    if (!hasTransaction) entityManager.getTransaction.begin()

    try {
      val b = block
      if (!hasTransaction) entityManager.getTransaction.commit()
      b
    } catch {
      case t: Throwable => {
        if (entityManager.getTransaction.isActive) entityManager.getTransaction.rollback()
        t.printStackTrace()
        throw new RuntimeException("Rolling back transaction because of uncaught exception", t)
      }
    }
  }
}