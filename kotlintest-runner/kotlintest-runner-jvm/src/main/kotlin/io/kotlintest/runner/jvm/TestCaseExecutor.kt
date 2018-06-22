package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestFilterResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class TestCaseExecutor(val listener: TestEngineListener,
                       val testCase: TestCase,
                       val context: TestContext) {

  fun execute() {
    try {

      listener.prepareTestCase(testCase)

      val listeners = listOf(testCase.spec) + testCase.spec.listeners() + Project.listeners()

      val extensions = testCase.config.extensions +
          testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      listeners.forEach { it.beforeTest(testCase.description) }

      fun onComplete(result: TestResult) {
        listeners.reversed().forEach { it.afterTest(testCase.description, result) }
        listener.completeTestCase(testCase, result)
      }

      fun interceptTestCase(remaining: List<TestCaseExtension>,
                            config: TestCaseConfig,
                            onComplete: (TestResult) -> Unit) {
        when {
          remaining.isEmpty() -> {
            val result = executeTestIfActive(testCase, config)
            onComplete(result)
          }
          else -> {
            val ctx = TestCaseInterceptContext(testCase.description, testCase.spec, config)
            remaining.first().intercept(ctx, { conf, callback -> interceptTestCase(remaining.drop(1), conf, callback) }, { onComplete(it) })
          }
        }
      }

      interceptTestCase(extensions, testCase.config, ::onComplete)

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.completeTestCase(testCase, TestResult.error(t))
    }
  }

  private fun executeTestIfActive(testCase: TestCase, config: TestCaseConfig): TestResult {

    val bang = testCase.name.startsWith("!") && System.getProperty("kotlintest.bang.disable") == null
    val tags = config.tags + testCase.spec.tags()
    val excluded = Project.testCaseFilters().map { it.filter(testCase.description) }.any { it == TestFilterResult.Ignore }
    val enabled = config.enabled && Project.tags().isActive(tags) && !bang && !excluded

    return if (enabled) {
      executeTestSet(TestSet(testCase, config.timeout, config.invocations, config.threads))
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
    val cleanExit = try {
      executor.awaitTermination(set.timeout.seconds, TimeUnit.SECONDS)
    } catch (e: InterruptedException) {
      false
    }
    val result = buildTestResult(cleanExit, set.timeout, error.get(), context.metaData())
    listener.completeTestSet(set, result)
    return result
  }

  /**
   * Creates the correct [TestResult] given the state of the test invocations.
   */
  private fun buildTestResult(cleanExit: Boolean, timeout: Duration, error: Throwable?, metadata: Map<String, Any?>): TestResult {
    return if (!cleanExit) {
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
