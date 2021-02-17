package io.kotest.extensions.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * WiremockPerTestListener starts the given wiremock server before every test and stop that
 * after every test.
 * */
class WiremockPerTestListener(private val server: WireMockServer) : TestListener {
   override suspend fun beforeTest(testCase: TestCase) {
      server.start()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      server.stop()
   }
}

/**
 * WiremockPerSpecListener starts the given wiremock server before spec execute first test and stop that
 * after spec completes.
 * */
class WiremockPerSpecListener(private val server: WireMockServer) : TestListener {
   override suspend fun beforeSpec(spec: Spec) {
      server.start()
   }

   override suspend fun afterSpec(spec: Spec) {
      server.stop()
   }
}
