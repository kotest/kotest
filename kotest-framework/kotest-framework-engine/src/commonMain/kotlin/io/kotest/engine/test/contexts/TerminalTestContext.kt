package io.kotest.engine.test.contexts

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] that errors on registration attempts of nested tests.
 */
class TerminalTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestContext {
   override suspend fun registerTestCase(nested: NestedTest) {
      error("Nested tests are not supported")
   }
}
