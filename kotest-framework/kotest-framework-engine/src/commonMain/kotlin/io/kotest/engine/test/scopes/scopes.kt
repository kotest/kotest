package io.kotest.engine.test.scopes

import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestScope] which uses the given [coroutineContext] with the other methods
 * delegating to the receiver context.
 */
internal fun TestScope.withCoroutineContext(coroutineContext: CoroutineContext): TestScope = when {
   this.coroutineContext == coroutineContext -> this
   this is TestScopeWithCoroutineContext -> TestScopeWithCoroutineContext(delegate, coroutineContext)
   else -> TestScopeWithCoroutineContext(this, coroutineContext)
}

private class TestScopeWithCoroutineContext(
   val delegate: TestScope,
   override val coroutineContext: CoroutineContext
) : TestScope by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"
}
