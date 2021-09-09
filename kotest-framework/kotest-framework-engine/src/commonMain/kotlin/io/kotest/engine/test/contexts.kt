package io.kotest.engine.test

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.createTestName
import io.kotest.core.test.toTestCase
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.listener.TestCaseListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
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
 * A [TestContext] that executes nested tests as they are discovered.
 */
class CallingThreadTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val duplicateTestNameMode: DuplicateTestNameMode,
   private val listener: TestEngineListener,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
) : TestContext {

   private val handler = DuplicateTestNameHandler(duplicateTestNameMode)
   private var failedfast = false

   // in the single instance runner we execute each nested test as soon as they are registered
   override suspend fun registerTestCase(nested: NestedTest) {
      log { "CallingThreadTestContext: Nested test case discovered $nested" }
      val overrideName = handler.handle(nested.name)?.let { createTestName(it) }
      val nestedTestCase = nested.toTestCase(testCase.spec, testCase, overrideName)
      if (failedfast) {
         log { "CallingThreadTestContext: A previous nested test failed and failfast is enabled - will mark this as ignored" }
         listener.testIgnored(nestedTestCase, "Failfast enabled on parent test")
      } else {
         val result = runTest(nestedTestCase, coroutineContext)
         if (testCase.config.failfast == true) {
            if (result.status == TestStatus.Failure || result.status == TestStatus.Error) {
               // if running this nested test results in an error, we won't launch any further nested tests
               failedfast = true
            }
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ): TestResult {

      return TestCaseExecutor(
         TestCaseListenerToTestEngineListenerAdapter(listener),
         defaultCoroutineDispatcherFactory,
      ).execute(
         testCase,
         CallingThreadTestContext(
            testCase,
            coroutineContext,
            duplicateTestNameMode,
            listener,
            defaultCoroutineDispatcherFactory
         )
      )
   }
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
      error("Nested tests are not supported")
   }
}
