package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import java.sql.Connection
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
 * Note: This extension requires Kotest 5.0+
 *
 * @param container the specific test container type
 * @param lifecycleMode determines how the container should be reset between tests
 *
 * @since 1.1.0
 */
@Deprecated("use JdbcDatabaseContainerExtension")
class JdbcTestContainerExtension(
   private val container: JdbcDatabaseContainer<Nothing>,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<TestContainerHikariConfig, DataSource>, AfterSpecListener, TestListener {

   private val ds = SettableDataSource(null)
   private var configure: TestContainerHikariConfig.() -> Unit = {}

   override fun mount(configure: TestContainerHikariConfig.() -> Unit): DataSource {
      this.configure = configure
      if (lifecycleMode == LifecycleMode.Spec) {
         container.start()
         ds.setDataSource(createDataSource())
      }
      return ds
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

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         stop()
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         start()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         stop()
      }
   }

   private suspend fun start() {
      withContext(Dispatchers.IO) {
         container.start()
         ds.setDataSource(createDataSource())
      }
   }

   private suspend fun stop() {
      withContext(Dispatchers.IO) {
         ds.setDataSource(null)
         container.stop()
      }
   }

   private fun runInitScripts(connection: Connection, dbInitScripts: List<String>) {

      val scriptRunner = ScriptRunner(connection)

      if (dbInitScripts.isNotEmpty()) {
         dbInitScripts.forEach {
            val resourceList = ResourceLoader().resolveResource(it)

            resourceList
               .filter { resource -> resource.endsWith(".sql") }
               .forEach { resource ->
                  scriptRunner.runScript(resource.loadToReader())
               }
         }
      }
   }
}

