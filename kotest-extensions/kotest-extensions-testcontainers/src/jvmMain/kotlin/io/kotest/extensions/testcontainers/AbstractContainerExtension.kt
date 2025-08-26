package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import org.testcontainers.containers.GenericContainer

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead")
abstract class AbstractContainerExtension<T : GenericContainer<*>>(
   private val container: T,
   private val mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
) : AfterProjectListener,
   AfterSpecListener,
   MountableExtension<T, T>,
   AutoCloseable {

   override fun mount(configure: T.() -> Unit): T {
      if (!container.isRunning) {
         container.configure()
         container.start()
      }
      return container
   }

   final override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   final override suspend fun afterSpec(spec: Spec) {
      if (mode == ContainerLifecycleMode.Spec && container.isRunning) close()
   }

   final override fun close() {
      container.stop()
   }
}
