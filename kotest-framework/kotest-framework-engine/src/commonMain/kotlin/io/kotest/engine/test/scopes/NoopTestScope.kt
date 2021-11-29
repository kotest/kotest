package io.kotest.engine.test.scopes

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
   override suspend fun registerTestCase(nested: NestedTest) {}
}
