package io.kotest.engine.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Wraps the [TestEngineListener] to listen for test events and returns an error
 * if there were no tests executed.
 */
internal object EmptyTestSuiteInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      // listens to engine events, and if we have a test finished, we know we didn't have an empty test suite
      var emptyTestSuite = true
      val mutex = Mutex()
      val emptyTestSuiteListener = object : TestEngineListener {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            mutex.withLock {
               emptyTestSuite = false
            }
         }
      }

      val result = execute(suite, CompositeTestEngineListener(listOf(listener, emptyTestSuiteListener)))

      return when {
         emptyTestSuite -> EngineResult(result.errors + EmptyTestSuiteException())
         else -> result
      }
   }
}

/**
 * Exception used to indicate that the engine had no specs to execute.
 */
class EmptyTestSuiteException : Exception("No specs were available to test")
