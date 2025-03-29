@file:Suppress("SqlResolve")

package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer

class JdbcTestContainerExtensionLeafTest : DescribeSpec() {
   init {

      val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
         withInitScript("init.sql")
         startupAttempts = 1
         withUrlParam("connectionTimeZone", "Z")
         withUrlParam("zeroDateTimeBehavior", "convertToNull")
      }

      val ds = install(JdbcTestContainerExtension(mysql, LifecycleMode.Leaf)) {
         maximumPoolSize = 8
         minimumIdle = 4
      }

      describe("context") {
         mysql.isRunning shouldBe false
         it("should initialize per leaf") {
            ds.connection.use {
               val rs1 = it.createStatement().executeQuery("SELECT * FROM hashtags")
               rs1.next()
               rs1.getString("tag") shouldBe "startrek"

               it.createStatement().executeUpdate("INSERT INTO hashtags(tag) VALUES ('foo')")

               val rs2 = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
               rs2.next()
               rs2.getLong(1) shouldBe 2
            }
         }

         it("this root test should have a different container container") {
            ds.connection.use {
               val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
               rs.next()
               rs.getLong(1) shouldBe 1
            }
         }
      }
   }
}
