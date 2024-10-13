package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.listener.TestEngineListener

/**
 * Notifies the [TestEngineListener]s that the engine is initialized.
 */
internal object TestEngineInitializedInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {
      context.listener.engineInitialized(context)
      return execute(context)
   }
}
