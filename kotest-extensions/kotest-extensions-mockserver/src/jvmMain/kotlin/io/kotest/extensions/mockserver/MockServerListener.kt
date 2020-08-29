package io.kotest.extensions.mockserver

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer

class MockServerListener(private val port: Int) : TestListener, AutoCloseable {

   // this has to be a var because MockServer starts the server as soon as you instantiate the instance :(
   var mockServer: ClientAndServer? = null

   override suspend fun beforeSpec(spec: Spec) {
      super.beforeSpec(spec)
      mockServer = startClientAndServer(port)
   }

   override suspend fun afterSpec(spec: Spec) {
      mockServer?.stop()
   }

   override fun close() {
      mockServer?.stop()
   }
}
