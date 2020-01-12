package io.kotest.runner.jvm

import io.kotest.Project
import io.kotest.core.*
import io.kotest.extensions.TestCaseExtension
import io.kotest.extensions.TestListener
import io.kotest.internal.unwrapIfReflectionCall
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * The [TestExecutor] is responsible for preparing and executing a single [TestCase].
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
@UseExperimental(ExperimentalTime::class)
class TestExecutor(private val listener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   suspend fun execute(testCase: TestCase, context: TestContext, onResult: (TestResult) -> Unit = { }) {
      logger.trace("Executing $testCase")

      val start = System.currentTimeMillis()
      try {

         try {
            before(testCase)
         } catch (t: Throwable) {
            logger.error("Before test errors", t)
            throw t
         }

         val extensions = testCase.config.extensions +
            testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
            Project.testCaseExtensions()

         // get active status here in case calling this function is expensive
         runExtensions(testCase, context, start, extensions) { result ->
            after(testCase, result)
            onResult(result)
         }

      } catch (t: Throwable) {
         t.printStackTrace()
      }
   }

   /**
    * Recursively runs the extensions until no extensions are left.
    * Each extension must invoke the callback given to it, or the test would hang.
    */
   private suspend fun runExtensions(
      testCase: TestCase,
      context: TestContext,
      start: Long,
      remaining: List<TestCaseExtension>,
      onComplete: suspend (TestResult) -> Unit
   ) {
      when {
         remaining.isEmpty() -> {
            val result = executeTestIfActive(testCase, context, start)
            onComplete(result)
         }
         else -> {
            remaining.first().intercept(
               testCase,
               { test, callback -> runExtensions(test, context, start, remaining.drop(1), callback) },
               { onComplete(it) }
            )
         }
      }
   }

   private suspend fun executeTestIfActive(testCase: TestCase, context: TestContext, start: Long): TestResult {
      val active = testCase.isActive()
      logger.trace("Test ${testCase.description.fullName()} active=$active")
      // executes the test case or if the test is not active then returns an ignored test result
      return if (active) executeActiveTest(testCase, context, start) else {
         listener.testIgnored(testCase, null)
         TestResult.Ignored
      }
   }

   private suspend fun executeActiveTest(testCase: TestCase, context: TestContext, start: Long): TestResult {
      logger.trace("Executing active test $testCase")
      listener.testStarted(testCase)

      // if we have more than one requested thread, we run the tests inside a clean executor;
      // otherwise we run on the same thread as the listeners to avoid issues where the before and
      // after listeners  require the same thread as the test case.
      // @see https://github.com/kotlintest/kotlintest/issues/447

      // we schedule a timeout, (if timeout has been configured) which will fail the test with a timed-out status
      val timeout = testCase.config.resolvedTimeout().toLongMilliseconds()

      // the test is executed in the same coroutine with a timeout
      val error = try {
         withTimeout(timeout) {
            collectAssertions(
               { testCase.test.invoke(context) },
               testCase.description.name,
               testCase.spec.resolvedAssertionMode()
            )
         }
      } catch (e: TimeoutCancellationException) {
         TimeoutException("Execution of test took longer than ${timeout}ms")
      } catch (e: Throwable) {
         e.unwrapIfReflectionCall()
      }

      val result = buildTestResult(error, emptyMap(), (System.currentTimeMillis() - start).milliseconds)
      logger.debug("Test completed with result $result")
      listener.testFinished(testCase, result)
      return result
   }

   /**
    * Handles all "before" listeners.
    */
   private fun before(testCase: TestCase) {
      logger.trace("before testCase ${testCase.description.fullName()}")
      val active = testCase.isActive()
      val userListeners = Project.listeners() // testCase.spec.listenerInstances + testCase.spec + Project.listeners()
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
      logger.trace("after testCase ${testCase.description.fullName()}")
      val active = testCase.isActive()
      val userListeners = Project.listeners() // testCase.spec.listenerInstances + testCase.spec + Project.listeners()
      userListeners.reversed().forEach {
         it.afterTest(testCase.description, result)
         if (active) {
            it.afterTest(testCase, result)
         }
      }
   }

   private fun buildTestResult(
      error: Throwable?,
      metadata: Map<String, Any?>,
      duration: Duration
   ): TestResult = when (error) {
      null -> successResult(duration)
      is AssertionError -> failureResult(error, duration)
      is SkipTestException -> ignoredResult(error.reason, duration)
      else -> errorResult(error, duration)
   }

   private fun successResult(duration: Duration) =
      TestResult(TestStatus.Success, null, null, duration, emptyMap())

   private fun ignoredResult(reason: String?, duration: Duration) =
      TestResult(TestStatus.Ignored, null, reason, duration, emptyMap())

   private fun errorResult(t: Throwable, duration: Duration) =
      TestResult(TestStatus.Error, t, null, duration, emptyMap())

   private fun failureResult(t: Throwable, duration: Duration) =
      TestResult(TestStatus.Failure, t, null, duration, emptyMap())
}
