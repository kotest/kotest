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
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s which is started the first time they are
 * installed in a spec. Upon installation, this extension returns an initialized [HikariDataSource]
 * connected to the database.
 *
 * If no spec is executed that installs a particular container,
 * then that container is never started.
 *
 * @param container the specific database test container type
 * @param beforeSpec a beforeSpec callback
 * If the container is never started, this callback will not be invoked.
 *
 * @param afterSpec an afterSpec callback
 * If the container is never started, this callback will not be invoked.
 *
 * @param beforeTest a beforeTest callback
 * If the container is never started, this callback will not be invoked.
 *
 * @param afterTest a afterTest callback
 * If the container is never started, this callback will not be invoked.
 *
 * @param beforeStart a callback that is invoked only once, just before the container is started.
 * If the container is never started, this callback will not be invoked.
 * This callback can be useful instead of the installation callback as it will only
 * be executed once, regardless of how many times this container is installed.
 *
 * @param afterStart a callback that is invoked only once, just after the container is started.
 * If the container is never started, this callback will not be invoked.
 *
 * @param beforeShutdown a callback that is invoked only once, just before the container is stopped.
 * If the container is never started, this callback will not be invoked.
 *
 * @param afterShutdown a callback that is invoked only once, just after the container is stopped.
 * If the container is never started, this callback will not be invoked.
 */
@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead")
class JdbcDatabaseContainerExtension(
   private val container: JdbcDatabaseContainer<*>,
   private val mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
   private val beforeStart: () -> Unit = {},
   private val afterStart: () -> Unit = {},
   private val beforeTest: suspend (TestCase, HikariDataSource) -> Unit = { _, _ -> },
   private val afterTest: suspend (TestCase, HikariDataSource) -> Unit = { _, _ -> },
   private val beforeSpec: suspend (Spec) -> Unit = { _ -> },
   private val afterSpec: suspend (Spec, HikariDataSource) -> Unit = { _, _ -> },
   private val beforeShutdown: (HikariDataSource) -> Unit = {},
   private val afterShutdown: () -> Unit = { },
) : MountableExtension<HikariDataSource, HikariDataSource>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   private val beforeSpecFn = beforeSpec
   private var dataSource: HikariDataSource? = null

   /**
    * Mounts the container, starting it if necessary. The [configure] block will be invoked
    * every time the container is mounted, and after the container has started.
    *
    * This will return an initialized [HikariDataSource].
    * The datasource will be closed in accordance with the provided [ContainerLifecycleMode].
    *
    * The datasource will be created with default options. If you wish to customize the datasource,
    * then ignore this returned value, and instead use the extension function [toDataSource] on the
    * container instance.
    */
   override fun mount(configure: HikariDataSource.() -> Unit): HikariDataSource {
      if (!container.isRunning) {
         beforeStart()
         container.start()
         dataSource = container.toDataSource()
         afterStart()
      }
      dataSource?.configure()
      return dataSource ?: error("Datasource not initialized")
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest(testCase, dataSource ?: error("Datasource not initialized"))
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest(testCase, dataSource ?: error("Datasource not initialized"))
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpecFn(spec)
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec(spec, dataSource ?: error("Datasource not initialized"))
      if (mode == ContainerLifecycleMode.Spec && container.isRunning) close()
   }

   override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   private suspend fun close() {
      withContext(Dispatchers.IO) {
         beforeShutdown(dataSource ?: error("Datasource not initialized"))
         dataSource?.close()
         container.stop()
         afterShutdown()
      }
   }
}

/**
 * Returns an initialized [HikariDataSource] connected to this [JdbcDatabaseContainer].
 *
 * @param configure a thunk to configure the [HikariConfig] used to create the datasource.
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
