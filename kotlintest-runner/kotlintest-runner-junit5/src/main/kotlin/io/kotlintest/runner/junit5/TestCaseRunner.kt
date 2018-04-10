package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.ConcurrentHashMap
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
    val metadata = ConcurrentHashMap<String, Any?>()

    return if (testCase.isActive()) {
      val errors = mutableListOf<Throwable>()
      for (j in 1..testCase.config.invocations) {
        executor.execute {
          try {
            val context = AsynchronousTestContext(testCase)
            testCase.test(context)
            context.blockUntilReady()
            metadata.putAll(context.metaData())
            val error = context.error()
            if (error != null)
              errors.add(error)
          } catch (t: Throwable) {
            errors.add(t)
          }
        }
      }

      executor.shutdown()
      val timeout = testCase.config.timeout
      val terminated = executor.awaitTermination(timeout.seconds, TimeUnit.SECONDS)

      if (!terminated) {
        TestResult(TestStatus.Error, TestTimedOutException(timeout.seconds, TimeUnit.SECONDS), null, metadata)
      } else {
        val first = errors.firstOrNull()
        when (first) {
          null -> TestResult(TestStatus.Success, null, null, metadata)
          is AssertionError -> TestResult(TestStatus.Failure, first, null, metadata)
          else -> TestResult(TestStatus.Error, first, null, metadata)
        }
      }
    } else {
      TestResult(TestStatus.Ignored, null, null)
    }
  }
}