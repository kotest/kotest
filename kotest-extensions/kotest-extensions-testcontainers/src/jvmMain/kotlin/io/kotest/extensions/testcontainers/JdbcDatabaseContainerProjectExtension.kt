package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.TestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import javax.sql.DataSource

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that will launch the container
 * upon first install, and close after the test suite has completed. This extension will only
 * launch the container once per project, and will not reset it between specs.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The pool can be configured in the mount configure method.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class JdbcDatabaseContainerProjectExtension(
   private val container: JdbcDatabaseContainer<*>,
) : MountableExtension<HikariConfig, DataSource>, AfterProjectListener, TestListener {

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

   override suspend fun afterProject() {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
