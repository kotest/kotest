package io.kotest.engine.test

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestContext] which uses the given [coroutineContext] with the other methods
 * delegating to the receiver context.
 */
internal fun TestContext.withCoroutineContext(coroutineContext: CoroutineContext): TestContext = when  {
   this.coroutineContext == coroutineContext -> this
   this is TestContextWithCoroutineContext -> TestContextWithCoroutineContext(delegate, coroutineContext)
   else -> TestContextWithCoroutineContext(this, coroutineContext)
}

private class TestContextWithCoroutineContext(
   val delegate: TestContext,
   override val coroutineContext: CoroutineContext
) : TestContext by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"
}

/**
 * A [TestContext] that ignores registration attempts of nested tests.
 */
class NoopTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestContext {
   override suspend fun registerTestCase(nested: NestedTest) {}
}

/**
 * A [TestContext] that errors on registration attempts of nested tests.
 */
class TerminalTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestContext {
   override suspend fun registerTestCase(nested: NestedTest) {
      error("Nested tests are not supported here")
   }
}
