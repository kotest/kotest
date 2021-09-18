package io.kotest.engine.interceptors

import io.kotest.core.config.configuration
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

internal class ProjectTimeoutEngineInterceptor(private val timeout: Long) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      return try {
         withTimeout(timeout) {
            execute(suite, listener)
         }
      } catch (e: TimeoutCancellationException) {
         val ee = ProjectTimeoutException(configuration.projectTimeout)
         EngineResult(listOf(ee))
      }
   }
}

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
class ProjectTimeoutException(val timeout: Long) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")
