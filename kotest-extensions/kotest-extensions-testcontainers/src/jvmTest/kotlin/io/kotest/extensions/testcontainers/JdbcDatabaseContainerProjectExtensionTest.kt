@file:Suppress("SqlResolve")

package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.mysql.MySQLContainer

private val mysql = MySQLContainer("mysql:8.0.26").apply {
   withInitScript("init.sql")
   startupAttempts = 1
   withUrlParam("connectionTimeZone", "Z")
   withUrlParam("zeroDateTimeBehavior", "convertToNull")
}

private val ext = JdbcDatabaseContainerProjectExtension(mysql)

@EnabledIf(LinuxOnlyGithubCondition::class)
class JdbcDatabaseContainerProjectExtensionTest1 : FunSpec() {
   init {

      val ds = install(ext) {
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

      // if this created another container, it would not have the value we inserted above
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
class JdbcDatabaseContainerProjectExtensionTest2 : FunSpec() {
   init {

      val ds = install(ext)

      // if this created another container, it would not have the value we inserted in the earlier spec
      test("another spec should use the same project level container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 2
         }
      }
   }
}
