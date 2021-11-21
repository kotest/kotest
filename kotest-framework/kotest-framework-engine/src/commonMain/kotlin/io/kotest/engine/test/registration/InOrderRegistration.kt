package io.kotest.engine.test.registration

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.mpp.log

/**
 * A [Registration] that executes nested tests as soon as they are discovered.
 */
internal class InOrderRegistration(
   private val listener: TestCaseExecutionListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : Registration {

   override suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult {
      log { "InOrderTestScope: Nested test case discovered $nested" }
      val nestedTestCase = Materializer(configuration).materialize(nested, parent)
      return TestCaseExecutor(
         listener,
         coroutineDispatcherFactory,
         configuration,
         DuplicateNameHandlingRegistration(
            parent.spec.duplicateTestNameMode ?: configuration.duplicateTestNameMode,
            this
         ),
      ).execute(nestedTestCase)
   }
}
