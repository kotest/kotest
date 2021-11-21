package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.registration.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

@ExperimentalCoroutinesApi
actual class TestCoroutineDispatcherInterceptor : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineDispatcherInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val dispatcher = TestCoroutineDispatcher()
      logger.log { Pair(testCase.name.testName, "Switching context to TestCoroutineDispatcher: $dispatcher") }
      return withContext(dispatcher) {
         test(testCase, scope.withCoroutineContext(dispatcher))
      }
   }
}

suspend fun main() {
   coroutineScope {
      println("c")
      val r = withContext(Dispatchers.IO) {
         withTimeoutOrNull(10) {
            println("d")
            try {
               delay(100)
               "foo"
            } catch (t: Throwable) {
               println(t.message)
               "bar"
            }
         }
      }
      println("Result=$r")
      println("f")
   }
   println("g")
}
