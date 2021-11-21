package io.kotest.engine.test.registration

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that errors on registration attempts of nested tests.
 */
class TerminalTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestScope {
}
