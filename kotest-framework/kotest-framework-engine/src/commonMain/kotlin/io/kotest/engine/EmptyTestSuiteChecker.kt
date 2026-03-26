package io.kotest.engine

import io.kotest.core.spec.style.scopes.TestDslState

internal object EmptyTestSuiteChecker {
   fun checkForEmptyTestSuite(
      context: TestEngineContext,
      result: EngineResult,
   ): EngineResult {
      return if (context.projectConfigResolver.failOnEmptyTestSuite() && context.collector.tests.isEmpty()) {
         result.addError(EmptyTestSuiteException())
      } else {
         result
      }
   }
}

internal object TestDslChecker {
   fun checkForDslState(result: EngineResult): EngineResult {
      return try {
         TestDslState.checkState()
         result
      } catch (e: Throwable) {
         result.addError(e)
      }
   }
}
