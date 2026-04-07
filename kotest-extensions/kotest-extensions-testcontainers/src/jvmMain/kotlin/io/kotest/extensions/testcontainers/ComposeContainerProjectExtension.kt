package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.testcontainers.containers.ComposeContainer
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/**
 * A Kotest [MountableExtension] for [ComposeContainer]s that will launch the container
 * upon first installation, and close after the test suite has completed. This extension will only
 * initalize the container once per project and will not reset it between specs regardless of how many
 * specs it is installed into.
 *
 * This is thread safe, so it is safe to be used in specs that run in parallel.
 *
 * @param container the specific test container type
 */
class ComposeContainerProjectExtension(
   private val container: ComposeContainer,
   private val onStart: ComposeContainer.() -> Unit = {},
) : MountableExtension<ComposeContainer, ComposeContainer>, AfterProjectListener {

   companion object {
      fun fromResource(resource: String): ComposeContainerProjectExtension {
         val file = this::class.java.classLoader.getResource(resource) ?: error("Resource not found: $resource")
         return ComposeContainerProjectExtension(ComposeContainer(File(file.path)))
      }
   }

   private val ref = AtomicReference<ComposeContainer>(null)
   private val lock = ReentrantLock()

   override fun mount(configure: ComposeContainer.() -> Unit): ComposeContainer {
      lock.lockInterruptibly()
      val t = ref.get()
      if (t == null) {
         configure(container)
         container.start()
         container.onStart()
         ref.set(container)
      }
      lock.unlock()
      return container
   }

   override suspend fun afterProject() {
      runInterruptible(Dispatchers.IO) {
         container.stop()
      }
   }
}
