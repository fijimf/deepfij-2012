package com.fijimf.deepfij.modelx

import org.hibernate.tool.hbm2ddl.SchemaExport
import java.io.File
import org.hibernate.ejb.Ejb3Configuration
import javax.persistence.{FlushModeType, EntityManagerFactory, Persistence, EntityManager}


object PersistenceSource {
  val persistenceUnitName = System.getProperty("deepfij.persistenceUnitName", "deepfij-test")
  val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName)
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()
  entityManager.setFlushMode(FlushModeType.AUTO)
  val cfg = new Ejb3Configuration().configure(persistenceUnitName, null).getHibernateConfiguration;
  val schemaExport = new SchemaExport(cfg)

  def main(args: Array[String]) {
    val tempFile = File.createTempFile("schema", ".sql")
    schemaExport.setOutputFile(tempFile.getAbsolutePath)
    schemaExport.execute(true, false, false, false)
  }
}