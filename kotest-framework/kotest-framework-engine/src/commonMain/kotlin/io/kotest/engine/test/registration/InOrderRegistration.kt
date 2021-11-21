package io.kotest.engine.test.registration

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.CoroutineContext

/**
 * A [Registration] that executes nested tests as soon as they are discovered.
 */
internal class InOrderRegistration(
   val testCase: TestCase,
   val coroutineContext: CoroutineContext,
   private val mode: DuplicateTestNameMode,
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : Registration {


   override suspend fun runNestedTestCase(nested: NestedTest): TestResult {
      log { "InOrderTestScope: Nested test case discovered $nested" }
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         coroutineDispatcherFactory,
         configuration,
      ).execute(testCase)
   }
}
