package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.extensions.TestListener
import io.kotlintest.internal.isActive
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

/**
 * The [TestCaseExecutor] is responsible for preparing and executing a single [TestCase].
 *
 * A test may be executed multiple times, if the config has set invocations > 1.
 * Also, tests may be executed concurrently if threads > 1.
 *
 * This class handles notifications to [TestListener] instances, as well as any
 * [TestCaseExtension] instances which may intercept and circumvent execution.
 *
 * A [TestCase] is only executed if it is considered active (see [isActive]),
 * otherwise a result of [TestResult.Ignored] is returned.
 *
 * Executing a [TestCase] is a blocking operation. Although each invocation of the
 * test closure will occur in a coroutine, the overall test execution will block
 * until all runs are completed and the final [TestResult] is returned.
 *
 * The executor can be shared between multiple tests as it is thread safe.
 */
class TestCaseExecutor(private val listener: TestEngineListener,
                       listenerExecutor: Executor) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val listenerDispatcher = listenerExecutor.asCoroutineDispatcher()

  suspend fun execute(testCase: TestCase, context: TestContext) {

    try {

      context.launch(listenerDispatcher) {
        before(testCase)
      }.join()

      val extensions = testCase.config.extensions +
          testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      runExtensions(testCase, context, extensions, testCase.config) { result ->
        context.launch(listenerDispatcher) {
          after(testCase, result)
        }.join()
      }

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.exitTestCase(testCase, TestResult.error(t))
    }
  }

  private suspend fun runExtensions(testCase: TestCase,
                                    context: TestContext,
                                    remaining: List<TestCaseExtension>,
                                    config: TestCaseConfig,
                                    onComplete: suspend (TestResult) -> Unit) {
    when {
      remaining.isEmpty() -> {
        val result = executeTestIfActive(testCase.copy(config = config), context)
        onComplete(result)
      }
      else -> {
        val ctx = TestCaseInterceptContext(testCase.description, testCase.spec, config)
        remaining.first().intercept(ctx, { conf, callback -> runExtensions(testCase, context, remaining.drop(1), conf, callback) }, { onComplete(it) })
      }
    }
  }

  private suspend fun executeTestIfActive(testCase: TestCase, context: TestContext): TestResult {

    // if we have more than one requested thread, we run the tests inside an executor,
    // otherwise we run on the main thread to avoid issues where before/after listeners
    // require the same thread as the test case. https://github.com/kotlintest/kotlintest/issues/447

    return if (isActive(testCase)) {

      listener.beforeTestCaseExecution(testCase)

      val dispatcher = when (testCase.config.threads) {
        1 -> listenerDispatcher
        else -> Executors.newFixedThreadPool(testCase.config.threads).asCoroutineDispatcher()
      }

      // captures an error from the test case closures
      val error = AtomicReference<Throwable?>(null)

      val job = context.launch {

        val jobs = (0 until testCase.config.invocations).map {
          launch(dispatcher) {
            listener.invokingTestCase(testCase, 1)
            testCase.test(context)
          }
        }
        jobs.forEach {
          it.invokeOnCompletion { e ->
            error.compareAndSet(null, e)
          }
          it.join()
        }
      }

      job.invokeOnCompletion { e ->
        error.compareAndSet(null, e)
      }
      job.join()
      val result = buildTestResult(error.get(), context.metaData())

      listener.afterTestCaseExecution(testCase, result)
      return result

    } else {
      TestResult.Ignored
    }
  }

  /**
   * Handles all "before" listeners.
   */
  private fun before(testCase: TestCase) {
    logger.debug("Before $testCase")
    listener.enterTestCase(testCase)
    val userListeners = listOf(testCase.spec) + testCase.spec.listeners() + Project.listeners()
    userListeners.forEach { it.beforeTest(testCase.description) }
  }

  /**
   * Handles all "after" listeners.
   */
  private fun after(testCase: TestCase, result: TestResult) {
    val userListeners = listOf(testCase.spec) + testCase.spec.listeners() + Project.listeners()
    userListeners.reversed().forEach { it.afterTest(testCase.description, result) }
    listener.exitTestCase(testCase, result)
  }

  private fun buildTestResult(error: Throwable?, metadata: Map<String, Any?>): TestResult = when (error) {
    null -> TestResult(TestStatus.Success, null, null, metadata)
    is AssertionError -> TestResult(TestStatus.Failure, error, null, metadata)
    else -> TestResult(TestStatus.Error, error, null, metadata)
  }
}