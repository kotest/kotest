package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

/**
 * Notifies the test listener on startup and shutdown.
 */
object TestEngineListenerInitializeFinalizeInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      listener.engineInitialize()
      val result = execute(suite, listener)
      listener.engineFinalize()
      return result
   }
}
