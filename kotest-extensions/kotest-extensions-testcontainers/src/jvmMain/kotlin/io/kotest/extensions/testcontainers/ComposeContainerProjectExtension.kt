package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.testcontainers.containers.ComposeContainer
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/**
 * A Kotest [MountableExtension] for [ComposeContainer]s that will launch the container
 * upon first install, and close after the spec has completed.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class ComposeContainerProjectExtension(
   private val container: ComposeContainer,
) : MountableExtension<ComposeContainer, ComposeContainer>, AfterProjectListener {

   private val ref = AtomicReference<ComposeContainer>(null)
   private val lock = ReentrantLock()

   override fun mount(configure: ComposeContainer.() -> Unit): ComposeContainer {
      lock.lockInterruptibly()
      val t = ref.get()
      if (t == null) {
         configure(container)
         container.start()
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
