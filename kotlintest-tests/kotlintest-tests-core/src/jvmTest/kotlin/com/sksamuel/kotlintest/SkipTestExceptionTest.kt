package com.sksamuel.kotlintest

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotlintest.*
import io.kotlintest.core.TestContext
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.FunSpec
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
