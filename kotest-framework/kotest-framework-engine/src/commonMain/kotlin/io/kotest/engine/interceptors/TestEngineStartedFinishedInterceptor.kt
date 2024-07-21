package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.core.Logger

/**
 * Notifies the test listener that the engine is ready to execute tests,
 * and the final [TestSuite] is ready to be used, and that all tests
 * have completed, with any unexpected errors.
 */
internal object TestEngineStartedFinishedInterceptor : EngineInterceptor {

   private val logger = Logger(TestEngineStartedFinishedInterceptor::class)

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      context.listener.engineStarted()
      val result = execute(context)

      result.errors.forEach {
         logger.log { Pair(null, "Error during test engine run: $it") }
         it.printStackTrace()
      }

      context.listener.engineFinished(result.errors)
      return result
   }
}
