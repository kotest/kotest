package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.events.invokeAllAfterTestCallbacks
import io.kotest.engine.events.invokeAllBeforeTestCallbacks
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis
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
   private val timeMark: TimeMark
) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      log { "LifecycleInterceptor: Executing active test '${testCase.descriptor.path().value}' with context $context" }
      listener.testStarted(testCase)

      testCase.invokeAllBeforeTestCallbacks()
         .fold(
            {
               createTestResult(timeMark.elapsedNow(), it).apply {
                  testCase.invokeAllAfterTestCallbacks(this)
               }
            },
            {
               val result = test(testCase, context)
               log { "LifecycleInterceptor: '${testCase.descriptor.path().value}'=${result}"  }
               // an error in the after test callbacks will override the result of the test if it was successfuls\
               // if the test already failed, that result will be used
               // todo combine into multiple errors ?
               testCase.invokeAllAfterTestCallbacks(result)
                  .fold(
                     {
                        when (result) {
                           is TestResult.Success, is TestResult.Ignored -> createTestResult(timeMark.elapsedNow(), it)
                           else -> result
                        }
                     },
                     { result }
                  )
            }
         )
   }
}
