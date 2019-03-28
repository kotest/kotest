package io.kotlintest.runner.jvm

import io.kotlintest.*
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestListener
import io.kotlintest.internal.isActive
import io.kotlintest.internal.unwrapIfReflectionCall
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.*
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
                       private val executor: ExecutorService,
                       private val scheduler: ScheduledExecutorService) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  suspend fun execute(testCase: TestCase, context: TestContext, onResult: (TestResult) -> Unit = { }) {

    try {

      // invoke the "before" callbacks here on the main executor
      context.launch(executor.asCoroutineDispatcher()) {
        before(testCase)
      }.join()

      val extensions = testCase.config.extensions +
          testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      // get active status here in case calling this function is expensive
      runExtensions(testCase, context, extensions) { result ->
        // invoke the "after" callbacks here on the main executor
        context.launch(executor.asCoroutineDispatcher()) {
          after(testCase, result)
        }.join()
        onResult(result)
      }

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.exitTestCase(testCase, TestResult.error(t))
    }
  }

  private suspend fun runExtensions(testCase: TestCase,
                                    context: TestContext,
                                    remaining: List<TestCaseExtension>,
                                    onComplete: suspend (TestResult) -> Unit) {
    when {
      remaining.isEmpty() -> {
        val result = executeTestIfActive(testCase, context)
        onComplete(result)
      }
      else -> {
        remaining.first().intercept(
            testCase,
            { test, callback -> runExtensions(test, context, remaining.drop(1), callback) },
            { onComplete(it) }
        )
      }
    }
  }

  private suspend fun executeTestIfActive(testCase: TestCase, context: TestContext): TestResult {
    val active = isActive(testCase)
    return if (active) executeTest(testCase, context) else TestResult.Ignored
  }

  // exectues the test case or if the test is not active then returns a ignored test result
  private suspend fun executeTest(testCase: TestCase, context: TestContext): TestResult {
    listener.beforeTestCaseExecution(testCase)

    // if we have more than one requested thread, we run the tests inside a clean executor;
    // otherwise we run on the same thread as the listeners to avoid issues where the before and
    // after listeners  require the same thread as the test case.
    // @see https://github.com/kotlintest/kotlintest/issues/447
    val executor = when (testCase.config.threads) {
      1 -> executor
      else -> Executors.newFixedThreadPool(testCase.config.threads)!!
    }

    val dispatcher = executor.asCoroutineDispatcher()

    // captures an error from the test case closures
    val error = AtomicReference<Throwable?>(null)

    val supervisorJob = context.launch {

      val testCaseJobs = (0 until testCase.config.invocations).map {
        // asynchronously disaptch the job and return any error
        async(dispatcher) {
          listener.invokingTestCase(testCase, 1)
          try {
            if (Project.globalAssertSoftly()) {
              assertSoftly {
                testCase.test(context)
              }
            } else {
              testCase.test(context)
            }
            null
          } catch (t: Throwable) {
            t.unwrapIfReflectionCall()
          }
        }
      }

      testCaseJobs.forEach {
        val testError = it.await()
        error.compareAndSet(null, testError)
      }
    }

    // we schedule a timeout, (if timeout has been configured) which will fail the test with a timed-out status
    if (testCase.config.timeout.nanos > 0) {
      scheduler.schedule({
        error.compareAndSet(null, TimeoutException("Execution of test took longer than ${testCase.config.timeout}"))
      }, testCase.config.timeout.nanos, TimeUnit.NANOSECONDS)
    }

    supervisorJob.invokeOnCompletion { e ->
      error.compareAndSet(null, e)
    }

    supervisorJob.join()

    // if the tests had their own special executor (ie threads > 1) then we need to shut it down
    if (testCase.config.threads > 1) {
      executor.shutdown()
    }

    val result = buildTestResult(error.get(), emptyMap())

    listener.afterTestCaseExecution(testCase, result)
    return result
  }

  /**
   * Handles all "before" listeners.
   */
  private fun before(testCase: TestCase) {
    listener.enterTestCase(testCase)

    val userListeners = testCase.spec.listenerInstances + testCase.spec + Project.listeners()
    val active = isActive(testCase)
    userListeners.forEach {
      it.beforeTest(testCase.description)
      if (active) {
        it.beforeTest(testCase)
      }
    }
  }

  /**
   * Handles all "after" listeners.
   */
  private fun after(testCase: TestCase, result: TestResult) {
    val active = isActive(testCase)
    val userListeners = testCase.spec.listenerInstances + testCase.spec + Project.listeners()
    userListeners.reversed().forEach {
      it.afterTest(testCase.description, result)
      if (active) {
        it.afterTest(testCase, result)
      }
    }
    listener.exitTestCase(testCase, result)
  }

  private fun buildTestResult(error: Throwable?, metadata: Map<String, Any?>): TestResult = when (error) {
    null -> TestResult(TestStatus.Success, null, null, metadata)
    is AssertionError -> TestResult(TestStatus.Failure, error, null, metadata)
    else -> TestResult(TestStatus.Error, error, null, metadata)
  }

}
