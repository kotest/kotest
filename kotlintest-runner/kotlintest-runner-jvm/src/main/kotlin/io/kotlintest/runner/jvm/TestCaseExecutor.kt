package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestListener
import io.kotlintest.internal.isActive
import io.kotlintest.internal.unwrapIfReflectionCall
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
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
                       private val listenerExecutor: ExecutorService,
                       private val scheduler: ScheduledExecutorService) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  suspend fun execute(testCase: TestCase, context: TestContext, onResult: (TestResult) -> Unit = { }) {

    try {

      context.launch(listenerExecutor.asCoroutineDispatcher()) {
        before(testCase)
      }.join()

      val extensions = testCase.config.extensions +
          testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      // get active status here in case calling this function is expensive (eg
      runExtensions(testCase, context, extensions) { result ->

        // it's possible the listenerExecutor has been shut down here.
        // If it has, we can only run them on another thread, better than a slap in the face
        // but will run foul of https://github.com/kotlintest/kotlintest/issues/447
        if (listenerExecutor.isShutdown) {
          context.launch {
            after(testCase, result)
          }.join()
        } else {
          context.launch(listenerExecutor.asCoroutineDispatcher()) {
            after(testCase, result)
          }.join()
        }

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
        remaining.first().intercept(testCase, { test, callback -> runExtensions(test, context, remaining.drop(1), callback) }, { onComplete(it) })
      }
    }
  }

  // exectues the test case or if the test is not active then returns a ignored test result
  private suspend fun executeTestIfActive(testCase: TestCase, context: TestContext): TestResult {

    return if (isActive(testCase)) {

      listener.beforeTestCaseExecution(testCase)

      // if we have more than one requested thread, we run the tests inside an executor,
      // otherwise we run on the same thread as the listeners to avoid issues where before/after listeners
      // require the same thread as the test case.
      // @see https://github.com/kotlintest/kotlintest/issues/447
      val executor = when (testCase.config.threads) {
        1 -> listenerExecutor
        else -> Executors.newFixedThreadPool(testCase.config.threads)!!
      }

      val dispatcher = executor.asCoroutineDispatcher()

      // captures an error from the test case closures
      val error = AtomicReference<Throwable?>(null)

      val supervisorJob = context.launch {

        val testCaseJobs = (0 until testCase.config.invocations).map {
          async(dispatcher) {
            listener.invokingTestCase(testCase, 1)
            try {
              testCase.test(context)
              null
            } catch(t: Throwable) {
              t.unwrapIfReflectionCall()
            }
          }
        }

        testCaseJobs.forEach {
          val testError = it.await()
          error.compareAndSet(null, testError)
        }
      }

      // we need to interrupt the threads in the executor in order to effect the timeout
      scheduler.schedule({
        error.compareAndSet(null, TimeoutException("Execution of test took longer than ${testCase.config.timeout}"))
        // this will ruin the listener executor so after() won't run if the test times out but I don't
        // know how else to interupt the coroutine context effectively. job.cancel() won't cut the mustard here.
        executor.shutdownNow()
      }, testCase.config.timeout.toMillis(), TimeUnit.MILLISECONDS)

      supervisorJob.invokeOnCompletion { e ->
        error.compareAndSet(null, e)
      }

      supervisorJob.join()
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
    listener.enterTestCase(testCase)

    val userListeners = testCase.spec.listeners() + testCase.spec + Project.listeners()
    userListeners.forEach {
      it.beforeTest(testCase.description)
      if (isActive(testCase)) {
        it.beforeTest(testCase)
      }
    }
  }

  /**
   * Handles all "after" listeners.
   */
  private fun after(testCase: TestCase, result: TestResult) {
    val userListeners = testCase.spec.listeners() + testCase.spec + Project.listeners()
    userListeners.reversed().forEach {
      it.afterTest(testCase.description, result)
      if (isActive(testCase)) {
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
