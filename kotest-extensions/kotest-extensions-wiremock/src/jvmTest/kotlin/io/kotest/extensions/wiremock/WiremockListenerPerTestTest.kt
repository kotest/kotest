package io.kotest.extensions.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL

@Suppress("BlockingMethodInNonBlockingContext")
class WiremockListenerPerTestTest : FunSpec({
   val wireMockServer = WireMockServer(9000)

   listener(WireMockListener.perTest(wireMockServer))
   listener(object : TestListener {
      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         shouldThrow<ConnectException> {
            val connection = URL("http://localhost:9000/test").openConnection() as HttpURLConnection
            connection.responseCode
         }
      }
   })


   test("should have started wiremock server") {
      wireMockServer.stubFor(
         get(urlEqualTo("/test"))
            .willReturn(ok())
      )
      val connection = URL("http://localhost:9000/test").openConnection() as HttpURLConnection
      connection.responseCode shouldBe 200
   }

   test("should have started wiremock server for second test as well") {
      wireMockServer.stubFor(
         get(urlEqualTo("/second-test"))
            .willReturn(ok())
      )
      val connection = URL("http://localhost:9000/second-test").openConnection() as HttpURLConnection
      connection.responseCode shouldBe 200
   }
})
