package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.TestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that will launch the container
 * upon first install, and close after the test suite has completed. This extension will only
 * launch the container once per project, and will not reset it between specs.
 *
 * Note: This extension requires Kotest 6.0+
 *
 * @param container the specific test container type
 */
class TestContainerProjectExtension(
   private val container: GenericContainer<*>,
) : MountableExtension<GenericContainer<*>, Unit>, AfterProjectListener, TestListener {

   override fun mount(configure: GenericContainer<*>.() -> Unit): Unit {
      configure(container)
      container.start()
   }

   override suspend fun afterProject() {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
