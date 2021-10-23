package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.EngineResult
import io.kotest.fp.Try

/**
 * Checks that we didn't have any partially constructed tests.
 */
@KotestInternal
internal object TestDslStateInterceptor : EngineInterceptor {
   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      val result = execute(context)
      return Try { TestDslState.checkState() }.fold(
         { EngineResult(listOf(it) + result.errors) },
         { result }
      )
   }
}
