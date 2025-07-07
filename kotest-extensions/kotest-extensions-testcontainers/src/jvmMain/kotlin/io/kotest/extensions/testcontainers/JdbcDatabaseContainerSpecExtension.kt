package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import javax.sql.DataSource

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that will launch the container
 * upon install, and close after the spec has completed.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The pool can be configured in the mount configure method.
 *
 * If you require a shared instance across multiple specs, consider using [JdbcDatabaseContainerProjectExtension]
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class JdbcDatabaseContainerSpecExtension(
   private val container: JdbcDatabaseContainer<*>,
) : MountableExtension<HikariConfig, DataSource>, AfterSpecListener {

   override fun mount(configure: HikariConfig.() -> Unit): DataSource {
      container.start()
      val config = HikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      val ds = HikariDataSource(config)
      return ds
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
