package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.Ignored
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer

@Ignored
class JdbcTestContainerExtensionTest : FunSpec() {
   init {

      val mysql = MySQLContainer("mysql:8.0.26").apply {
         withInitScript("init.sql")
         startupAttempts = 1
         withUrlParam("connectionTimeZone", "Z")
         withUrlParam("zeroDateTimeBehavior", "convertToNull")
      }

      val ds = install(JdbcTestContainerExtension(mysql)) {
         maximumPoolSize = 8
         minimumIdle = 4
      }

      test("should initialize per spec") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT * FROM hashtags")
            rs.next()
            rs.getString("tag") shouldBe "startrek"

            it.createStatement().executeUpdate("INSERT INTO hashtags(tag) VALUES ('foo')")

            val rs2 = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs2.next()
            rs2.getLong(1) shouldBe 2
         }
      }

      test("another test should have the same container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 2
         }
      }
   }
}
