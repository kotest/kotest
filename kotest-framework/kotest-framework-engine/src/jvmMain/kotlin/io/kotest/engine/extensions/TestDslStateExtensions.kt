package io.kotest.engine.extensions

import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

/**
 * Checks that we didn't have any partially constructed tests.
 */
internal object TestDslStateExtensions : EngineExtension {
   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      TestDslState.checkState()
      return execute(suite, listener)
   }
}
