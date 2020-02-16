package io.kotest.extensions.mockserver

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer

class MockServerListener(private val port: Int) : TestListener {

   private var mockServer: ClientAndServer? = null

   override suspend fun beforeSpec(spec: Spec) {
      super.beforeSpec(spec)
      mockServer = startClientAndServer(1080)
   }

   override suspend fun afterSpec(spec: Spec) {
      mockServer?.stop();
   }
}
