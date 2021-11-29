package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.engine.EngineResult
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.listener.Node
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.Logger
import kotlin.time.TimeSource

/**
 * Notifies the test listener that the engine is ready to execute tests,
 * and the final [TestSuite] is ready to be used, and that all tests
 * have completed, with any unexpected errors.
 */
@KotestInternal
internal object TestEngineStartedFinishedInterceptor : EngineInterceptor {

   private val logger = Logger(TestEngineStartedFinishedInterceptor::class)

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      context.listener.executionStarted(Node.Engine(context))
      val result = execute(context)
      val start = TimeSource.Monotonic.markNow()

      result.errors.forEach {
         logger.log { Pair(null, "Error during test engine run: $it") }
         it.printStackTrace()
      }

      val error = when {
         result.errors.isEmpty() -> null
         result.errors.size == 1 -> result.errors.first()
         else -> MultipleExceptions(result.errors)
      }

      context.listener.executionFinished(Node.Engine(context), createTestResult(start.elapsedNow(), error))
      return result
   }
}
