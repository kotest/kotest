package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.GenericContainer
import java.io.File

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
 * @param beforeSpec a beforeSpec callback
 * @param afterSpec an afterSpec callback
 * @param beforeTest a beforeTest callback
 * @param afterTest a afterTest callback
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
class DockerComposeContainerExtension<T : DockerComposeContainer<*>>(
   private val container: T,
   private val mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
   private val beforeStart: (T) -> Unit = {},
   private val afterStart: (T) -> Unit = {},
   private val beforeTest: suspend (TestCase, T) -> Unit = { _, _ -> },
   private val afterTest: suspend (TestCase, T) -> Unit = { _, _ -> },
   private val beforeSpec: suspend (Spec, T) -> Unit = { _, _ -> },
   private val afterSpec: suspend (Spec, T) -> Unit = { _, _ -> },
   private val beforeShutdown: (T) -> Unit = {},
   private val afterShutdown: (T) -> Unit = {},
) : MountableExtension<T, T>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   companion object {
      operator fun invoke(composeFile: File): DockerComposeContainerExtension<*> =
         DockerComposeContainerExtension(DockerComposeContainer(composeFile))

      operator fun invoke(
         composeFile: File,
         mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
      ): DockerComposeContainerExtension<*> =
         DockerComposeContainerExtension(DockerComposeContainer(composeFile), mode)
   }

   private var started: Boolean = false

   /**
    * Mounts the container, starting it if necessary. The [configure] block will be invoked
    * every time the container is mounted, and after the container has started.
    */
   override fun mount(configure: T.() -> Unit): T {
      if (!started) {
         beforeStart(container)
         container.start()
         started = true
         afterStart(container)
      }
      container.configure()
      return container
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest(testCase, container)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest(testCase, container)
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec(spec, container)
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec(spec, container)
      if (mode == ContainerLifecycleMode.Spec && started) close()
   }

   override suspend fun afterProject() {
      if (started) close()
   }

   private suspend fun close() {
      withContext(Dispatchers.IO) {
         beforeShutdown(container)
         container.stop()
         started = false
         afterShutdown(container)
      }
   }
}
