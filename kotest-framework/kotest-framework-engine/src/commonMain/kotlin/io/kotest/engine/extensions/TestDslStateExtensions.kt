package io.kotest.engine.extensions

import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.fp.Try

/**
 * Checks that we didn't have any partially constructed tests.
 */
internal object TestDslStateExtensions : EngineExtension {
   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      val result = execute(suite, listener)
      return Try { TestDslState.checkState() }.fold(
         { EngineResult(listOf(it) + result.errors) },
         { result }
      )
   }
}
