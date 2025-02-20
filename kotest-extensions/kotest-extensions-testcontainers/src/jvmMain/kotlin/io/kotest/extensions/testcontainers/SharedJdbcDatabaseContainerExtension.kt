package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.containers.JdbcDatabaseContainer
import java.sql.Connection

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that are started the first time they are
 * installed in a test, and then shared throughout the same gradle module. The container is shutdown
 * after all specs have completed.
 *
 * If no spec is executed that installs a particular container, then that container is never started.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The Hikari pool can be configured in the constructor through the [configure] parameter, or through
 * the install method per spec. If the latter option is used, then only the configure function from
 * the install where the container is first started will be executed.
 *
 * Note: This extension requires Kotest 5.0+
 *
 * @param container the specific database test container type
 * @param beforeSpec a beforeSpec callback
 * @param afterSpec an afterSpec callback
 * @param beforeTest a beforeTest callback
 * @param afterTest a afterTest callback
 * @param afterStart called one time, after the container is started
 * @param configure a callback to configure the [HikariConfig] instance that is used to create the [HikariDataSource].
 *
 * @since 1.3.0
 */
@Deprecated("use JdbcDatabaseContainerExtension")
class SharedJdbcDatabaseContainerExtension(
   private val container: JdbcDatabaseContainer<*>,
   private val beforeTest: suspend (HikariDataSource) -> Unit = {},
   private val afterTest: suspend (HikariDataSource) -> Unit = {},
   private val beforeSpec: suspend (HikariDataSource) -> Unit = {},
   private val afterSpec: suspend (HikariDataSource) -> Unit = {},
   private val afterStart: (HikariDataSource) -> Unit = {},
   private val configure: TestContainerHikariConfig.() -> Unit = {},
) : MountableExtension<TestContainerHikariConfig, HikariDataSource>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   private var ds: HikariDataSource? = null

   override fun mount(configure: TestContainerHikariConfig.() -> Unit): HikariDataSource {
      if (!container.isRunning) {
         container.start()
         ds = createDataSource().apply(afterStart)
      }
      return ds ?: error("DataSource was not initialized")
   }

   override suspend fun afterProject() {
      if (container.isRunning) container.stop()
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec(ds ?: error("DataSource was not initialized"))
   }

   private fun runInitScripts(connection: Connection, dbInitScripts: List<String>) {
      if (dbInitScripts.isNotEmpty()) {
         val scriptRunner = ScriptRunner(connection)
         dbInitScripts.forEach { script ->
            ResourceLoader()
               .resolveResource(script)
               .filter { it.endsWith(".sql") }
               .forEach { scriptRunner.runScript(it.loadToReader()) }
         }
      }
   }

   private fun createDataSource(): HikariDataSource {
      val config = TestContainerHikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      val ds = HikariDataSource(config)
      runInitScripts(ds.connection, config.dbInitScripts)
      return ds
   }
}
