package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

internal object ProjectTimeoutEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      return when (val timeout = context.configuration.projectTimeout) {
         null -> execute(context)
         else -> try {
            withTimeout(timeout) {
               execute(context)
            }
         } catch (e: TimeoutCancellationException) {
            val ee = ProjectTimeoutException(timeout)
            EngineResult(listOf(ee))
         }
      }

   }
}

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
class ProjectTimeoutException(val timeout: Duration) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")
