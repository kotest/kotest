package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.engine.EngineResult
import io.kotest.engine.listener.TestEngineListener

/**
 * Notifies the [TestEngineListener]s that the engine is initialized.
 */
@KotestInternal
internal object TestEngineInitializedInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      context.listener.engineInitialized(context)
      return execute(context)
   }
}
