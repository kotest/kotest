package io.kotlintest.runner.jvm

import arrow.core.Try
import arrow.core.recover
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.internal.NamedThreadFactory
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Executes a [TestSet], blocking until all runs have completed.
 *
 * The [TestResult] returned will be calculated from the result of each run, with
 * errors taking precedence over failures, and success being returned only if no
 * errors or failures are detected.
 */
class TestSetExecutor(val listener: TestEngineListener, val context: TestContext) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  fun execute(set: TestSet): TestResult {

    // if we have more than one requested thread, we run the tests inside an executor,
    // otherwise we run on the main thread to avoid issues where before/after listeners
    // require the same thread as the test case. https://github.com/kotlintest/kotlintest/issues/447

    // captures an error from the test closures
    val error = AtomicReference<Throwable?>(null)

    when (set.threads) {
      1 -> {
        Try {
          for (k in 0 until set.invocations) {
            listener.testRun(set, k)
            set.testCase.test(context)
          }
        }.recover {
          error.compareAndSet(null, it)
        }
      }
      else -> {
        val executor = Executors.newFixedThreadPool(set.threads, NamedThreadFactory("kotlintest-test-executor-%d"))
        // submitting a task might fail if the executor has already been stopped so we just Try {} here
        Try {
          for (k in 0 until set.invocations) {
            executor.submit {
              Try {
                listener.testRun(set, k)
                set.testCase.test(context)
              }.recover {
                error.compareAndSet(null, it)
                // if an error is detected we'll abort any further invocations
                executor.shutdownNow()
              }
            }
          }
        }

        executor.shutdown()

        try {

          logger.debug("Waiting ${set.timeout.seconds} seconds for test set to complete")
          executor.awaitTermination(set.timeout.seconds, TimeUnit.SECONDS)

        } catch (e: RejectedExecutionException) {
          error.compareAndSet(null, e)

        } catch (e: InterruptedException) {
          logger.error("Interrupted waiting for executor to complete", e.message)
          error.compareAndSet(null, TestTimedOutException(set.timeout.seconds, TimeUnit.SECONDS))
        }
      }
    }

    val result = buildTestResult(error.get(), context.metaData())
    listener.completeTestSet(set, result)
    return result
  }

  /**
   * Creates the correct [TestResult] given the state of the test invocations.
   */
  private fun buildTestResult(error: Throwable?, metadata: Map<String, Any?>): TestResult = when (error) {
    null -> TestResult(TestStatus.Success, null, null, metadata)
    is AssertionError -> TestResult(TestStatus.Failure, error, null, metadata)
    else -> TestResult(TestStatus.Error, error, null, metadata)
  }
}