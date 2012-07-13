package com.fijimf.deepfij.modelx

import org.hibernate.tool.hbm2ddl.SchemaExport
import org.hibernate.ejb.Ejb3Configuration
import javax.persistence._
import org.hibernate.exception.SQLGrammarException
import java.io.File


object PersistenceSource {
  val persistenceUnitName = System.getProperty("deepfij.persistenceUnitName", "deepfij")
  val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName)
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()
  entityManager.setFlushMode(FlushModeType.AUTO)
  val cfg = new Ejb3Configuration().configure(persistenceUnitName, null).getHibernateConfiguration
  val schemaExport = new SchemaExport(cfg)

  def buildDatabase() {
    schemaExport.execute(false, true, false, false)
  }

  def dropDatabase() {
    schemaExport.drop(false, true)
  }

  def testDatabase(q: String = "SELECT * FROM schedule"): Boolean = {
    val qry: Query = entityManager.createNativeQuery(q)
    try {
      qry.getResultList
      true
    }
    catch {
      case pe: PersistenceException => {
        if (pe.getCause.isInstanceOf[SQLGrammarException]) {
          false
        } else {
          throw pe
        }
      }
    }
  }

  //  def main(args: Array[String]) {
  //    dropDatabase()
  //    println(testDatabase())
  //
  //    buildDatabase()
  //    println(testDatabase())
  //
  //  }
  //
  def main(args: Array[String]) {

    val tempFile = File.createTempFile("schema", ".sql")
    schemaExport.setOutputFile(tempFile.getAbsolutePath)
    schemaExport.execute(true, false, false, false)
    println(tempFile.getAbsolutePath)

  }
}