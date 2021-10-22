package io.kotest.engine.test.contexts

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.configuration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.engine.listener.TestEngineListener
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
 * Creates a [TestContext] suitable for use in a single instance runner.
 */
fun createSingleInstanceTestContext(
   testCase: TestCase,
   coroutineContext: CoroutineContext,
   mode: DuplicateTestNameMode,
   listener: TestEngineListener,
   dispatcherFactory: CoroutineDispatcherFactory
): TestContext {
   return DuplicateNameHandlingTestContext(
      testCase.spec.duplicateTestNameMode ?: configuration.duplicateTestNameMode,
      InOrderTestContext(
         testCase,
         coroutineContext,
         mode,
         listener,
         dispatcherFactory
      )
   )
}
