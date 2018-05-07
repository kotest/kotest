package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestCase
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class TestCaseExecutor(val listener: TestEngineListener,
                       val case: TestCase,
                       val context: TestContext) {

  fun execute() {
    try {

      listener.prepareTestCase(case)

      val listeners = listOf(case.spec) + case.spec.listeners() + Project.listeners()

      val extensions = case.config.extensions +
          case.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      listeners.forEach { it.beforeTest(case.description) }

      fun onComplete(result: TestResult) {
        listeners.reversed().forEach { it.afterTest(case.description, result) }
        listener.completeTestCase(case, result)
      }

      fun interceptTestCase(remaining: List<TestCaseExtension>,
                            config: TestCaseConfig,
                            onComplete: (TestResult) -> Unit) {
        when {
          remaining.isEmpty() -> {
            val result = executeTestIfActive(config)
            onComplete(result)
          }
          else -> {
            val ctx = TestCaseInterceptContext(case.description, case.spec, config)
            remaining.first().intercept(ctx, { conf, callback -> interceptTestCase(remaining.drop(1), conf, callback) }, { onComplete(it) })
          }
        }
      }

      interceptTestCase(extensions, case.config, ::onComplete)

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.completeTestCase(case, TestResult.error(t))
    }
  }

  private fun executeTestIfActive(config: TestCaseConfig): TestResult {
    return if (config.enabled && Project.tags().isActive(config.tags)) {
      executeTestSet(TestSet(case, config.timeout, config.invocations, config.threads))
    } else {
      TestResult.Ignored
    }
  }

  /**
   * Executes a [TestSet] using the given values for invocations, threads and timeout.
   * This function will block until all runs have completed.
   *
   * The [TestResult] returned will be calculated from the result of each run, with
   * errors taking precedence over failures, and success being returned only if no
   * errors of failure are detected.
   */
  private fun executeTestSet(set: TestSet): TestResult {
    listener.prepareTestSet(set)

    // each test set runs inside its own execution service, so we can easily support multiple threads
    val executor = Executors.newFixedThreadPool(set.threads)

    // captures an error from the test closures
    val error = AtomicReference<Throwable?>(null)

    for (j in 0 until set.invocations) {
      executor.execute {
        try {
          listener.testRun(set, j)
          set.testCase.test(context)
        } catch (t: Throwable) {
          error.set(t)
          // if an error is detected we'll abort any further invocations
          executor.shutdownNow()
        }
      }
    }

    executor.shutdown()
    val terminated = executor.awaitTermination(set.timeout.seconds, TimeUnit.SECONDS)
    val result = buildTestResult(terminated, set.timeout, error.get(), context.metaData())
    listener.completeTestSet(set, result)
    return result
  }

  /**
   * Creates the correct [TestResult] given the state of the test invocations.
   */
  private fun buildTestResult(terminated: Boolean, timeout: Duration, error: Throwable?, metadata: Map<String, Any?>): TestResult {
    return if (!terminated) {
      TestResult(TestStatus.Error, TestTimedOutException(timeout.seconds, TimeUnit.SECONDS), null, metadata)
    } else {
      when (error) {
        null -> TestResult(TestStatus.Success, null, null, metadata)
        is AssertionError -> TestResult(TestStatus.Failure, error, null, metadata)
        else -> TestResult(TestStatus.Error, error, null, metadata)
      }
    }
  }
}

/**
 * A testset comprises the parameters of a particular [TestCase]s execution.
 */
data class TestSet(val testCase: TestCase,
                   val timeout: Duration,
                   val invocations: Int,
                   val threads: Int)
