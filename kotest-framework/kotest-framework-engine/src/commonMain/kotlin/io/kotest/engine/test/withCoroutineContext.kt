package io.kotest.engine.test

import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestContext] which uses the given [coroutineContext] with the other methods
 * delegating to the receiver context.
 */
internal fun TestContext.withCoroutineContext(coroutineContext: CoroutineContext): TestContext = when (this) {
   is TestContextWithCoroutineContext -> TestContextWithCoroutineContext(delegate, coroutineContext)
   else -> TestContextWithCoroutineContext(this, coroutineContext)
}

private class TestContextWithCoroutineContext(
   val delegate: TestContext,
   override val coroutineContext: CoroutineContext
) : TestContext by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"
}
