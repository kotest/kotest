package io.kotest.extensions.mockserver

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer

/**
 * @param port A mockserver will be launched for each [port]. If empty, a random port
 * will be used (found via [mockServer])
 */
@Deprecated("Use MockServerExtension. Deprecated since 6.0")
class MockServerListener(
   private vararg val port: Int = intArrayOf()
) : TestListener, AutoCloseable {

   // this has to be a var because MockServer starts the server as soon as you instantiate the instance :(
   var mockServer: ClientAndServer? = null

   // we can use beforeSpec if we're registering this in project configuration
   override suspend fun beforeSpec(spec: Spec) {
      super.beforeSpec(spec)
      if (mockServer == null)
         mockServer = startClientAndServer(*port.toTypedArray())
   }

   // we need beforeTest because registering as an inline listener will occur after beforeSpec is invoked
   override suspend fun beforeTest(testCase: TestCase) {
      super.beforeTest(testCase)
      if (mockServer == null)
         mockServer = startClientAndServer(*port.toTypedArray())
   }

   override suspend fun afterSpec(spec: Spec) {
      mockServer?.stop()
   }

   override fun close() {
      mockServer?.stop()
   }
}
