package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.createTestResult
import io.kotest.core.Logger
import kotlin.time.TimeMark

/**
 * Executes a test taking care of invoking user level listeners.
 * The test is always marked as started at this stage.
 *
 * If the before-test listeners fail, then the test is not executed, but the after-test listeners
 * are executed, and the returned result contains the listener exception.
 *
 * If the test itself fails, then the after-test listeners are executed,
 * and the returned result is generated from the test exception.
 *
 * If the after-test listeners fail, then the returned result is taken from the listener exception
 * and any result from the test itself is ignored.
 *
 * Essentially, the after-test listeners are always attempted, and any error from invoking the before, test,
 * or after code is returned as higher priority than the result from the test case itself.
 */
internal class LifecycleInterceptor(
   private val listener: TestCaseExecutionListener,
   private val timeMark: TimeMark,
   registry: ExtensionRegistry,
) : TestExecutionInterceptor {

   private val extensions = TestExtensions(registry)
   private val logger = Logger(LifecycleInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      logger.log { Pair(testCase.name.testName, "Notifying listener test started") }
      listener.testStarted(testCase)

      return extensions.beforeTestBeforeAnyBeforeContainer(testCase)
         .fold(
            {
               val result = test(testCase, scope)
               // any error in the after listeners will override the test result unless the test was already an error
               extensions
                  .afterTestAfterAnyAfterContainer(testCase, result)
                  .fold(
                     { result },
                     { if (result.isErrorOrFailure) result else createTestResult(timeMark.elapsedNow(), it) }
                  )
            },
            {
               val result = createTestResult(timeMark.elapsedNow(), it)
               // can ignore errors here as we already have the before errors to show
               extensions.afterTestAfterAnyAfterContainer(testCase, result)
               result
            }
         )
   }
}
