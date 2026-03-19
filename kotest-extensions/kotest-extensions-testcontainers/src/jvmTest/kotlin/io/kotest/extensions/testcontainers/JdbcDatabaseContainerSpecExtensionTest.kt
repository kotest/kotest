@file:Suppress("SqlResolve")

package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.install
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.mysql.MySQLContainer

private val mysql = MySQLContainer("mysql:8.0.26").apply {
   withInitScript("init.sql")
   startupAttempts = 1
   withUrlParam("connectionTimeZone", "Z")
   withUrlParam("zeroDateTimeBehavior", "convertToNull")
}

private val extension = JdbcDatabaseContainerSpecExtension(mysql)

@EnabledIf(LinuxOnlyGithubCondition::class)
@Order(7)
class JdbcDatabaseContainerSpecExtensionTest1 : FunSpec() {
   init {

      val ds = install(extension) {
         maximumPoolSize = 8
         minimumIdle = 4
      }

      test("should initialize once per module") {
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

      // if this created another container, it would not have the value we inserted earlier
      test("another test should use the same container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 2
         }
      }
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
@Order(8)
class JdbcDatabaseContainerSpecExtensionTest2 : FunSpec() {
   init {

      val ds = install(extension)

      // if this created another container, it would not have the value we inserted earlier
      test("another spec should create a new container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 1
         }
      }
   }
}
