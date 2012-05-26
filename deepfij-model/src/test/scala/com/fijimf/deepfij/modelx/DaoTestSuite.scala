package com.fijimf.deepfij.modelx

import org.scalatest.{BeforeAndAfterEach, FunSuite}


trait DaoTestSuite extends FunSuite with BeforeAndAfterEach {

  override def beforeEach() {
    PersistenceSource.buildDatabase()
    PersistenceSource.entityManager.clear()
  }

  override protected def afterEach() {
    PersistenceSource.dropDatabase()
  }
}