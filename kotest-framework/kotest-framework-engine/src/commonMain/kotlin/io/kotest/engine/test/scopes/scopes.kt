package io.kotest.engine.test.scopes

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.listener.TestEngineListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

/**
 * Returns a new [TestScope] which uses the given [coroutineContext] with the other methods
 * delegating to the receiver context.
 */
internal fun TestScope.withCoroutineContext(coroutineContext: CoroutineContext): TestScope = when  {
   this.coroutineContext == coroutineContext -> this
   this is TestScopeWithCoroutineContext -> TestScopeWithCoroutineContext(delegate, coroutineContext)
   else -> TestScopeWithCoroutineContext(this, coroutineContext)
}

/**
 * Returns a new [TestScope] which uses the given [coroutineTestScope] with the other methods
 * delegating to the receiver context.
 */
@ExperimentalCoroutinesApi
internal fun TestScope.withCoroutineTestScope(coroutineTestScope: kotlinx.coroutines.test.TestScope): TestScope = when  {
   this is TestScopeWithCoroutineTestScope -> TestScopeWithCoroutineTestScope(delegate, coroutineTestScope)
   else -> TestScopeWithCoroutineTestScope(this, coroutineTestScope)
}

private class TestScopeWithCoroutineContext(
   val delegate: TestScope,
   override val coroutineContext: CoroutineContext
) : TestScope by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"
}

@ExperimentalCoroutinesApi
private class TestScopeWithCoroutineTestScope(
   val delegate: TestScope,
   override val testScope: kotlinx.coroutines.test.TestScope
) : TestScope by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"

   override val coroutineContext: CoroutineContext
      get() = testScope.coroutineContext
}

/**
 * Creates a [TestScope] suitable for use in a single instance runner.
 */
fun createSingleInstanceTestScope(
   testCase: TestCase,
   coroutineContext: CoroutineContext,
   mode: DuplicateTestNameMode,
   listener: TestEngineListener,
   dispatcherFactory: CoroutineDispatcherFactory,
   configuration: ProjectConfiguration,
): TestScope {
   return DuplicateNameHandlingTestScope(
      testCase.spec.duplicateTestNameMode ?: configuration.duplicateTestNameMode,
      InOrderTestScope(
         testCase,
         coroutineContext,
         mode,
         listener,
         dispatcherFactory,
         configuration,
      )
   )
}
