package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

/**
 * Notifies the [TestEngineListener] on startup and shutdown.
 */
object TestEngineShutdownInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      listener.engineStartup()
      val result = execute(suite, listener)
      listener.engineShutdown()
      return result
   }
}
