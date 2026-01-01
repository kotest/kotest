package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that will launch the container
 * upon first install, and close after the spec has completed.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class TestContainerSpecExtension<T : GenericContainer<*>>(
   private val container: T,
   private val options: TestContainerOptions = TestContainerOptions(),
) : MountableExtension<T, T>, AfterSpecListener, TestListener {

   override fun mount(configure: T.() -> Unit): T {
      configure(container)
      if (options.log)
         container.withLogConsumer { print(it.utf8String) }
      container.start()
      if (options.log)
         container.followOutput { print(it.utf8String) }
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
