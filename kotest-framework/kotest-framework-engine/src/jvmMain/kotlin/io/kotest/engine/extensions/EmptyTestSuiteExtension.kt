package io.kotest.engine.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Wraps the [TestEngineListener] to listen for test events and errors if there were no tests executed.
 */
internal object EmptyTestSuiteExtension : EngineExtension {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      // listens to engine events, and if we have a test finished, we know we didn't have an empty test suite
      val emptyTestSuite = AtomicBoolean(true)
      val emptyTestSuiteListener = object : TestEngineListener {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            emptyTestSuite.set(false)
         }
      }

      val result = execute(suite, CompositeTestEngineListener(listOf(listener, emptyTestSuiteListener)))

      return when {
         emptyTestSuite.get() -> EngineResult(result.errors + RuntimeException("No tests were executed"))
         else -> result
      }
   }
}
