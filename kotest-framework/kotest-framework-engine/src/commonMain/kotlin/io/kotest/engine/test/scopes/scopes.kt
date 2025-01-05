package io.kotest.engine.test.scopes

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
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

/**
 * Creates a [TestScope] suitable for use in a single instance runner.
 */
fun createSingleInstanceTestScope(
   testCase: TestCase,
   specContext: SpecContext,
   coroutineContext: CoroutineContext,
   mode: DuplicateTestNameMode,
   context: EngineContext,
): TestScope {
   return DuplicateNameHandlingTestScope(
      testCase.spec.duplicateTestNameMode ?: context.configuration.duplicateTestNameMode,
      InOrderTestScope(
         specContext,
         testCase,
         coroutineContext,
         mode,
         context,
      )
   )
}
