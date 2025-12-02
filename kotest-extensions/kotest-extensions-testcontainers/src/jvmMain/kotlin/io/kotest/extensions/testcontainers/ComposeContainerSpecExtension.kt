package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.testcontainers.containers.ComposeContainer

/**
 * A Kotest [MountableExtension] for [ComposeContainer]s that will launch the container
 * upon first install, and close after the spec has completed.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class ComposeContainerSpecExtension(
   private val container: ComposeContainer,
) : MountableExtension<ComposeContainer, ComposeContainer>, AfterSpecListener, TestListener {

   override fun mount(configure: ComposeContainer.() -> Unit): ComposeContainer {
      configure(container)
      container.start()
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      runInterruptible(Dispatchers.IO) {
         container.stop()
      }
   }
}
