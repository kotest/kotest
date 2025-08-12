package io.kotest.engine.interceptors

import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.EngineResult

/**
 * Checks that we didn't have any partially constructed tests.
 */
internal object TestDslStateInterceptor : EngineInterceptor {
   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {
      val result = execute(context)
      return runCatching { TestDslState.checkState() }.fold(
         { result },
         { result.addError(it) },
      )
   }
}
