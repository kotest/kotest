package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.TestEngineListener

/**
 * Wraps the [TestEngineListener] to listen for test events and returns an error
 * if there were no tests executed and [failOnEmptyTestSuite] is configured to be true.
 */
internal object EmptyTestSuiteInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      return when (context.configuration.failOnEmptyTestSuite) {
         true -> {
            val collector = CollectingTestEngineListener()
            val result = execute(context.mergeListener(collector))
            when {
               collector.tests.isEmpty() -> EngineResult(result.errors + EmptyTestSuiteException)
               else -> result
            }
         }
         false -> execute(context)
      }
   }
}

/**
 * Exception used to indicate that the engine had no specs to execute.
 */
object EmptyTestSuiteException : Exception("No specs were available to test")
