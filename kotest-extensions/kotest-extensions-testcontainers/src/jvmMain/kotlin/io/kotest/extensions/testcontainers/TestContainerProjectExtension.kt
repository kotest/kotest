package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that will launch the container
 * upon first install, and close after the test suite has completed. This extension will only
 * launch the container once per project, and will not reset it between specs.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class TestContainerProjectExtension<T : GenericContainer<*>>(
   private val container: T,
   private val options: TestContainerOptions = TestContainerOptions(),
) : MountableExtension<T, T>, AfterProjectListener {

   private val ref = AtomicReference<T>(null)
   private val lock = ReentrantLock()

   override fun mount(configure: T.() -> Unit): T {
      lock.lockInterruptibly()
      val t = ref.get()
      if (t == null) {
         configure(container)
         container.withLogConsumer(BasicLogConsumer(options.logs))
         container.start()
         container.followOutput(BasicLogConsumer(options.logs))
         ref.set(container)
      }
      lock.unlock()
      return container
   }

   override suspend fun afterProject() {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
