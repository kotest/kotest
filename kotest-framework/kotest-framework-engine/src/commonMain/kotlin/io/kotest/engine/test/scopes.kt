package io.kotest.engine.test

import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestScope] which uses the given [coroutineContext].
 * Optimizes for the cases where the replacing coroutineContext is the same instance.
 */
internal fun TestScope.withCoroutineContext(coroutineContext: CoroutineContext): TestScope =
   when (this.coroutineContext) {
      coroutineContext -> this
      else -> DefaultTestScope(this.testCase, coroutineContext)
   }
