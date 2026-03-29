package io.kotest.engine.test.scopes

import io.kotest.core.spec.TestDefinition
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that ignores registration attempts of nested tests.
 */
class NoopTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestScope {
   @Deprecated("Use TestDefinition. Will be removed in 7.0")
   override suspend fun registerTestCase(nested: NestedTest) {
   }

   override suspend fun registerTest(test: TestDefinition) {}
}
