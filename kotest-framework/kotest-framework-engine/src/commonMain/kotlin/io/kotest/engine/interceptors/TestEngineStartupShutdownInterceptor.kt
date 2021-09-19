package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.listener.TestEngineListener

/**
 * Notifies the [TestEngineListener] on startup and shutdown.
 */
internal object TestEngineStartupShutdownInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      context.listener.engineStartup()
      val result = execute(context)
      context.listener.engineShutdown()
      return result
   }
}
