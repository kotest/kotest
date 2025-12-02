package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that are started the first time they are
 * installed in a test, and then shared throughout the same gradle module. The container is shutdown
 * after all specs have completed.
 *
 * If no spec is executed that installs a particular container, then that container is never started.
 *
 * The returned materialized value can be adapted through the [mapper] parameter, to allow returning something other
 * than the raw container. For example, you could return a RedisClient that was preconnected to a redis container,
 * rather than returning the container itself.
 *
 * Note: This extension requires Kotest 5.0+
 *
 * @param container the specific database test container type
 * @param beforeSpec a beforeSpec callback, can be used to configure the container.
 * @param afterSpec an afterSpec callback, can be used to configure the container.
 * @param beforeTest a beforeTest callback, can be used to configure the container.
 * @param afterTest a afterTest callback, can be used to configure the container.
 * @param configure called one time after the container is started. Can configure the container without needing to
 * specify the configuration code at every use site.
 * @param mapper optional mapping function to adapt the materialized value.
 *
 * @since 1.3.0
 */
@Deprecated("use ContainerExtension. Will be removed in 6.2")
class SharedTestContainerExtension<T : GenericContainer<*>, U>(
   private val container: T,
   private val beforeTest: suspend (T) -> Unit = {},
   private val afterTest: suspend (T) -> Unit = {},
   private val beforeSpec: suspend (T) -> Unit = {},
   private val afterSpec: suspend (T) -> Unit = {},
   private val configure: T.() -> Unit = {},
   private val mapper: T.() -> U,
) : MountableExtension<T, U>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   companion object {
      operator fun <T : GenericContainer<*>> invoke(
         container: T,
         beforeTest: (T) -> Unit = {},
         afterTest: (T) -> Unit = {},
         beforeSpec: (T) -> Unit = {},
         afterSpec: (T) -> Unit = {},
         configure: T.() -> Unit = {},
      ): SharedTestContainerExtension<T, T> {
         return SharedTestContainerExtension(
            container,
            beforeTest,
            afterTest,
            beforeSpec,
            afterSpec,
            configure
         ) { this }
      }
   }

   override fun mount(configure: T.() -> Unit): U {
      if (!container.isRunning) {
         container.start()
         configure(container)
         this@SharedTestContainerExtension.configure(container)
      }
      return this@SharedTestContainerExtension.mapper(container)
   }

   override suspend fun afterProject() {
      if (container.isRunning) container.stop()
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest(container)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest(container)
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec(container)
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec(container)
   }
}
