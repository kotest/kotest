package com.sksamuel.kotest.timeout

import com.nhaarman.mockito_kotlin.mock
import io.kotest.core.Description
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.spec.SpecConfiguration
import io.kotest.runner.jvm.TestExecutor
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalTime
class TestCaseTimeoutListenerTest : FunSpec() {

  private var listenerRan = false

  override fun afterTest(testCase: TestCase, result: TestResult) {
    listenerRan = true
  }

  override fun afterSpec(spec: SpecConfiguration) {
    listenerRan shouldBe true
  }

  init {

    test("tests which timeout should still run the 'after test' listeners").config(timeout = 1000.milliseconds) {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val scheduler = Executors.newScheduledThreadPool(1)
      val executor = TestExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), this@TestCaseTimeoutListenerTest) {
        Thread.sleep(500)
      }.copy(config = TestCaseConfig(true, invocations = 1, threads = 1, timeout = 125.milliseconds))

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)
    }
  }
}
