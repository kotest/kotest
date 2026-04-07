package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.testcontainers.containers.ComposeContainer
import java.io.File

/**
 * A Kotest [MountableExtension] for [ComposeContainer]s that will launch the container
 * upon first installation, and close after the spec has completed.
 *
 * @param container the specific test container type
 */
class ComposeContainerSpecExtension(
   private val container: ComposeContainer,
   private val onStart: ComposeContainer.() -> Unit = {},
) : MountableExtension<ComposeContainer, ComposeContainer>, AfterSpecListener, TestListener {

   companion object {
      fun fromResource(resource: String): ComposeContainerSpecExtension {
         val file = this::class.java.classLoader.getResource(resource) ?: error("Resource not found: $resource")
         return ComposeContainerSpecExtension(ComposeContainer(File(file.path)))
      }
   }

   override fun mount(configure: ComposeContainer.() -> Unit): ComposeContainer {
      configure(container)
      container.start()
      container.onStart()
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      runInterruptible(Dispatchers.IO) {
         container.stop()
      }
   }
}
