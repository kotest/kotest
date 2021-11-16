package io.kotest.engine.test.registration

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.defaultTestScope
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.Logger

class InOrderRegistration(
   private val testCase: TestCase,
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : Registration {

   private val logger = Logger(this::class)

   override suspend fun registerNestedTest(nested: NestedTest): TestResult? {
      logger.log { Pair(testCase.name.testName, "Nested test case discovered $nested") }
      val nestedTestCase = Materializer(configuration).materialize(nested, testCase)
      return runTest(nestedTestCase)
   }

   private suspend fun runTest(testCase: TestCase): TestResult {
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         coroutineDispatcherFactory,
         configuration,
         FailFastRegistration(
            testCase,
            configuration,
            listener,
            DuplicateNameHandlingRegistration(
               configuration.duplicateTestNameMode,
               InOrderRegistration(testCase, listener, coroutineDispatcherFactory, configuration)
            ),
         )
      ).execute(testCase, defaultTestScope(testCase))
   }
}
