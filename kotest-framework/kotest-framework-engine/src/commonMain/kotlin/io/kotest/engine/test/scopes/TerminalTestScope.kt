package io.kotest.engine.test.scopes

import io.kotest.core.spec.TestDefinition
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that errors on registration attempts of nested tests.
 */
internal class TerminalTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestScope {
   @Deprecated("Use TestDefinition. Will be removed in 7.0")
   override suspend fun registerTestCase(nested: NestedTest) {
      error("Nested tests are not supported")
   }

   override suspend fun registerTest(test: TestDefinition) {
      error("Nested tests are not supported")
   }
}
