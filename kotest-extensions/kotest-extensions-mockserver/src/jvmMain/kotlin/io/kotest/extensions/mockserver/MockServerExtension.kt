package io.kotest.extensions.mockserver

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer

/**
 * @param port A mockserver will be launched for each [port]. If empty, a random port
 * will be used (found via [mockServer])
 */
class MockServerExtension(
   private vararg val port: Int = intArrayOf()
) : MountableExtension<String, ClientAndServer>, BeforeSpecListener, AfterSpecListener {

   // this has to be a var because MockServer starts the server as soon as you instantiate the instance
   private var mockServer: ClientAndServer? = null

   override fun mount(configure: String.() -> Unit): ClientAndServer {
      mockServer = startClientAndServer(*port.toTypedArray())
      return mockServer ?: error("Bug: startClientAndServer should have returned an instance")
   }

   override suspend fun afterSpec(spec: Spec) {
      mockServer?.stop()
   }
}
