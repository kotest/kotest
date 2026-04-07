package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.options.ContainerExtensionConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that will launch the container
 * upon first installation, and close after the spec has completed.
 *
 * @param container the specific test container type
 * @param onStart a callback that is invoked after the container is started
 */
class TestContainerSpecExtension<T : GenericContainer<*>>(
   private val container: T,
   private val config: ContainerExtensionConfig = ContainerExtensionConfig(),
   private val onStart: T.() -> Unit = {},
) : MountableExtension<T, T>, AfterSpecListener, TestListener {

   override fun mount(configure: T.() -> Unit): T {
      configure(container)
      container.start()
      onStart(container)
      container.followOutput(config.logConsumer)
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
