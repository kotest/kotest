package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer
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
   private val options: TestContainerOptions = TestContainerOptions(),
) : MountableExtension<HikariConfig, DataSource>, AfterSpecListener {

   override fun mount(configure: HikariConfig.() -> Unit): DataSource {
      container.withLogConsumer(BasicLogConsumer(options.logs))
      container.start()
      container.followOutput(BasicLogConsumer(options.logs))
      val config = HikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      val ds = HikariDataSource(config)
      return ds
   }

   override suspend fun afterSpec(spec: Spec) {
      runInterruptible(Dispatchers.IO) {
         container.stop()
      }
   }
}

class BasicLogConsumer(private val type: LogTypes) : Consumer<OutputFrame> {
   override fun accept(t: OutputFrame) {
      when (t.type) {
         OutputFrame.OutputType.STDOUT if (type == LogTypes.STDOUT || type == LogTypes.ALL) -> println(t.utf8String)
         OutputFrame.OutputType.STDERR if (type == LogTypes.STDERR || type == LogTypes.ALL) -> println(t.utf8String)
         OutputFrame.OutputType.END -> println(t.utf8String)
         else -> Unit
      }
   }
}

data class TestContainerOptions(
   val logs: LogTypes = LogTypes.NONE
)

enum class LogTypes { NONE, STDOUT, STDERR, ALL }
