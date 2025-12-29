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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that are started the first time they are
 * installed in a spec.
 *
 * If no spec is executed that installs a particular container,
 * then that container is never started.
 *
 * Containers can be shared between specs using the default [mode].
 *
 * @param container the test container instance
 *
 * @param mode determines if the container is shutdown after the test suite (project) or after the installed spec
 *             The default is after the test suite.
 *
 * @param beforeStart a callback that is invoked only once, just before the container is started.
 * If the container is never started, this callback will not be invoked.
 * This callback can be useful instead of the installation callback as it will only
 * be executed once, regardless of how many times this container is installed.
 *
 * @param afterStart a callback that is invoked only once, just after the container is started.
 * If the container is never started, this callback will not be invoked.
 *
 * @param beforeSpec a beforeSpec callback.
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
 * @param beforeShutdown a callback that is invoked only once, just before the container is stopped.
 * If the container is never started, this callback will not be invoked.
 *
 * @param afterShutdown a callback that is invoked only once, just after the container is stopped.
 * If the container is never started, this callback will not be invoked.
 */
@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
class ContainerExtension<T : GenericContainer<*>>(
   private val container: T,
   private val mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
   private val beforeStart: () -> Unit = {},
   private val afterStart: () -> Unit = {},
   private val beforeTest: suspend (TestCase) -> Unit = { _ -> },
   private val afterTest: suspend (TestCase) -> Unit = { _ -> },
   private val beforeSpec: suspend (Spec) -> Unit = { _ -> },
   private val afterSpec: suspend (Spec) -> Unit = { _ -> },
   private val beforeShutdown: () -> Unit = {},
   private val afterShutdown: () -> Unit = {},
) : MountableExtension<T, T>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   /**
    * Mounts the container, starting it if necessary. The [configure] block will be invoked
    * every time the container is mounted, and after the container has started.
    */
   override fun mount(configure: T.() -> Unit): T {
      if (!container.isRunning) {
         beforeStart()
         container.start()
         afterStart()
      }
      container.configure()
      return container
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest.invoke(testCase)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest.invoke(testCase)
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec.invoke(spec)
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec.invoke(spec)
      if (mode == ContainerLifecycleMode.Spec && container.isRunning) close()
   }

   override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   private suspend fun close() {
      withContext(Dispatchers.IO) {
         beforeShutdown()
         container.stop()
         afterShutdown()
      }
   }
}
