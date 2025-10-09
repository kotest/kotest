package io.kotest.extensions.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URI

@Suppress("BlockingMethodInNonBlockingContext")
class WiremockListenerPerTestTest : FunSpec({
   val wireMockServer = WireMockServer(9000)

   extension(object : TestListener {
      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         shouldThrow<ConnectException> {
            val connection = URI.create("http://localhost:9000/test").toURL().openConnection() as HttpURLConnection
            connection.responseCode
         }
      }
   })
   extension(WireMockListener.perTest(wireMockServer))

   test("should have started wiremock server") {
      wireMockServer.stubFor(
         get(urlEqualTo("/test"))
            .willReturn(ok())
      )
      val connection = URI.create("http://localhost:9000/test").toURL().openConnection() as HttpURLConnection
      connection.responseCode shouldBe 200
   }

   test("should have started wiremock server for second test as well") {
      wireMockServer.stubFor(
         get(urlEqualTo("/second-test"))
            .willReturn(ok())
      )
      val connection = URI.create("http://localhost:9000/second-test").toURL().openConnection() as HttpURLConnection
      connection.responseCode shouldBe 200
   }
})
