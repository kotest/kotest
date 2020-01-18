package io.kotest.runner.jvm

import io.kotest.core.config.Project
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.extensions.TestListener
import io.kotest.core.internal.unwrapIfReflectionCall
import io.kotest.core.spec.resolvedExtensions
import io.kotest.core.spec.resolvedListeners
import io.kotest.core.test.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.lang.AssertionError
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
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
 * The executor can be shared between multiple tests as it is thread safe.
 */
@UseExperimental(ExperimentalTime::class)
class TestExecutor(private val listener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   /**
    * Executes the given [TestCase] using the supplied [TestContext] and invoking [onResult]
    * with the outcome of the test.
    */
   suspend fun execute(testCase: TestCase, context: TestContext, onResult: (TestResult) -> Unit) {
      logger.trace("Evaluating $testCase")

      val active = testCase.isActive()
      logger.trace("Test ${testCase.description.fullName()} active=$active")

      // if the test case is active we execute it, otherwise we just invoke the callback with ignored
      when (active) {
         true -> executeActiveTest(testCase, context, onResult)
         false -> {
            listener.testIgnored(testCase, null)
            onResult(TestResult.Ignored)
         }
      }
   }

   private suspend fun executeActiveTest(
      testCase: TestCase,
      context: TestContext,
      onResult: (TestResult) -> Unit
   ) {
      logger.trace("Executing active test $testCase")

      val start = System.currentTimeMillis()
      listener.testStarted(testCase)

      val extensions = testCase.config.extensions +
         testCase.spec.resolvedExtensions().filterIsInstance<TestCaseExtension>() +
         Project.extensions().filterIsInstance<TestCaseExtension>()

      try {
         notifyBeforeTest(testCase)
         runExtensions(testCase, context, start, extensions) { result ->
            notifyAfterTest(testCase, result)
            if (result.status != TestStatus.Ignored)
               listener.testFinished(testCase, result)
            onResult(result)
         }
      } catch (e: AssertionError) {
         val result = TestResult.error(e, (System.currentTimeMillis() - start).milliseconds)
         listener.testFinished(testCase, result)
         onResult(result)
      } catch (e: Exception) {
         val result = TestResult.error(e, (System.currentTimeMillis() - start).milliseconds)
         listener.testFinished(testCase, result)
         onResult(result)
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
            val result = invokeTestCase(testCase, context, start)
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

   private suspend fun invokeTestCase(testCase: TestCase, context: TestContext, start: Long): TestResult {
      logger.trace("invokeTestCase $testCase")

      // if we have more than one requested thread, we run the tests inside a clean executor;
      // otherwise we run on the same thread as the listeners to avoid issues where the before and
      // after listeners  require the same thread as the test case.
      // @see https://github.com/kotlintest/kotlintest/issues/447

      // we calculate the timeout, (if timeout has been configured) which will fail the test with a timed-out status
      val timeout = testCase.config.resolvedTimeout().toLongMilliseconds()

      val error = try {

         // we use a scope here so we can wait for nested coroutines to launch
         coroutineScope {

            val scope = this

            // we need to wrap the test context in one that extends the outer scope otherwise
            // launched coroutines will take place on the test executors own coroutine
            val contextp = object : TestContext() {
               override val testCase: TestCase = context.testCase
               override suspend fun registerTestCase(test: NestedTest) = context.registerTestCase(test)
               override val coroutineContext: CoroutineContext = scope.coroutineContext
            }

            // we ensure the timeout is honoured
            withTimeout(timeout) {
               validateAssertions(
                  { testCase.test.invoke(contextp) },
                  testCase.description.name,
                  testCase.spec.resolvedAssertionMode()
               )
            }
         }
      } catch (e: TimeoutCancellationException) {
         TimeoutException("Execution of test took longer than ${timeout}ms")
      } catch (e: Throwable) {
         e.unwrapIfReflectionCall()
      } catch (e: AssertionError) {
         e
      }

      val result = buildTestResult(error, emptyMap(), (System.currentTimeMillis() - start).milliseconds)
      logger.debug("Test completed with result $result")
      return result
   }

   /**
    * Notifies the user listeners that a [TestCase] is starting.
    * The listeners will not be invoked if the test case is disabled.
    */
   private suspend fun notifyBeforeTest(testCase: TestCase) {
      logger.trace("Executing listeners beforeTest")
      val listeners = testCase.spec.resolvedListeners()
      listeners.forEach {
         it.beforeTest(testCase)
      }
   }

   /**
    * Notifies the user listeners that a [TestCase] has finished.
    * The listeners will not be invoked if the test case is disabled.
    */
   private suspend fun notifyAfterTest(testCase: TestCase, result: TestResult) {
      logger.trace("Executing listeners afterTest")
      val listeners = testCase.spec.resolvedListeners()
      listeners.forEach {
         it.afterTest(testCase, result)
      }
   }
}
