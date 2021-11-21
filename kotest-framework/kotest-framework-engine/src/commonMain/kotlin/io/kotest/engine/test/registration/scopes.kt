package io.kotest.engine.test.registration

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestScope] which wraps the same [TestCase] but uses the given [coroutineContext].
 */
internal fun TestScope.withCoroutineContext(coroutineContext: CoroutineContext): TestScope {
   return when (this.coroutineContext) {
      coroutineContext -> this
      else -> DefaultTestScope(this.testCase, coroutineContext)
   }
}

class DefaultTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestScope
