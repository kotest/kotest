package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.JdbcDatabaseContainer

/**
 * Returns an initialized [com.zaxxer.hikari.HikariDataSource] connected to this [org.testcontainers.containers.JdbcDatabaseContainer].
 *
 * @param configure a thunk to configure the [com.zaxxer.hikari.HikariConfig] used to create the datasource.
 */
fun JdbcDatabaseContainer<*>.toDataSource(configure: HikariConfig.() -> Unit = {}): HikariDataSource {
   val config = HikariConfig()
   config.jdbcUrl = jdbcUrl
   config.username = username
   config.password = password
   config.minimumIdle = 0
   config.configure()
   return HikariDataSource(config)
}
