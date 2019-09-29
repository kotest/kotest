package com.sksamuel.kotest

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotest.Description
import io.kotest.SkipTestException
import io.kotest.TestCase
import io.kotest.TestStatus
import io.kotest.core.TestContext
import io.kotest.runner.jvm.TestCaseExecutor
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.specs.FreeSpec
import io.kotest.specs.FunSpec
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.Executors

class SkipTestExceptionTest : FunSpec() {

  private val scheduler = Executors.newScheduledThreadPool(1)

  init {
    test("A test that throws SkipTestException should have Ignored as a result") {

      val listenerExecutor = Executors.newSingleThreadExecutor()
      val listener = mock<TestEngineListener> {}
      val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)

      val testCase = TestCase.test(Description.spec("wibble"), object : FreeSpec() {}) {
        throw SkipTestException("Foo")
      }

      val context = object : TestContext(GlobalScope.coroutineContext) {
        override suspend fun registerTestCase(testCase: TestCase) {}
        override fun description(): Description = Description.spec("wibble")
      }
      executor.execute(testCase, context)

      then(listener).should().exitTestCase(argThat { description == Description.spec("wibble") }, argThat { status == TestStatus.Ignored && reason == "Foo" })

    }
  }
}
