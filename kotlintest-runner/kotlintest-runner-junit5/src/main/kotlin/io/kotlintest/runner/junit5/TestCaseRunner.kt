package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs a [TestCase] returning a [TestResult] describing
 * the outcome of the test.
 *
 * Will run the test multiple times on one or more thread,
 * as controlled by the [TestCaseConfig]
 */
object TestCaseRunner {

  fun runTest(testCase: TestCase): TestResult {

    val executor = Executors.newFixedThreadPool(testCase.config.threads)

    return if (testCase.isActive()) {

      val context = TestCaseContext(testCase)

      val errors = mutableListOf<Throwable>()
      for (j in 1..testCase.config.invocations) {
        executor.execute {
          try {
            testCase.test(context)
            val result = context.future().get()
            if (result != null)
              errors.add(result)
          } catch (t: Throwable) {
            errors.add(t)
          }
        }
      }

      executor.shutdown()
      val timeout = testCase.config.timeout
      val terminated = executor.awaitTermination(timeout.seconds, TimeUnit.SECONDS)

      if (!terminated) {
        TestResult(TestStatus.Failed, TestTimedOutException(timeout.seconds, TimeUnit.SECONDS), context.metaData())
      } else if (errors.isEmpty()) {
        TestResult(TestStatus.Passed, null, context.metaData())
      } else {
        TestResult(TestStatus.Failed, errors.firstOrNull(), context.metaData())
      }

    } else {
      TestResult(TestStatus.Ignored, null)
    }
  }
}