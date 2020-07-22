package io.kotest.core.runtime

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.core.test.isActive
import io.kotest.core.test.resolvedTimeout
import io.kotest.core.test.resolvedInvocationTimeout
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
data class TimeoutException constructor(val duration: Duration) :
   Exception("Test did not completed within ${duration.toLongMilliseconds()}ms")

/**
 * Executes a single [TestCase]. Uses a [TestCaseExecutionListener] to notify callers of events in the test.
 *
 * The [TimeoutExecutionContext] is used to provide a way of executing functions on the underlying platform
 * in a way that best utilizes threads or the lack of on that platform.
 *
 * The [validateTestCase] function is invoked before a test is executed to ensure that the
 * test case is compatible on the actual platform. For example, in JS we can only support certain
 * spec styles due to limitations in the underlying test runners.
 *
 * If the given test case is invalid, then this method should throw an exception.
 */
@OptIn(ExperimentalTime::class)
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val executionContext: TimeoutExecutionContext,
   private val validateTestCase: (TestCase) -> Unit = {}
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      validateTestCase(testCase)
      val mark = TimeSource.Monotonic.markNow()
      return intercept(testCase, context, mark, testCase.extensions()).apply {
         when (status) {
            TestStatus.Ignored -> listener.testIgnored(testCase)
            else -> listener.testFinished(testCase, this)
         }
      }
   }

   /**
    * Recursively runs the extensions until no extensions are left, at which point the test
    * is executed and the result returned.
    */
   private suspend fun intercept(
      testCase: TestCase,
      context: TestContext,
      mark: TimeMark,
      extensions: List<TestCaseExtension>
   ): TestResult {
      return when {
         extensions.isEmpty() -> executeIfActive(testCase) { executeActiveTest(testCase, context, mark) }
         else -> extensions.first().intercept(testCase) { intercept(it, context, mark, extensions.drop(1)) }
      }
   }

   /**
    * Checks the active status of a [TestCase] before invoking it.
    * If the test is inactive, then [TestResult.ignored] is returned.
    */
   private suspend fun executeIfActive(testCase: TestCase, ifActive: suspend () -> TestResult): TestResult {
      // if the test case is active we execute it, otherwise we just invoke the callback with ignored
      return when (testCase.isActive()) {
         true -> {
            log("${testCase.description.path()} is active")
            ifActive()
         }
         false -> {
            log("${testCase.description.path()} is *not* active")
            TestResult.Ignored
         }
      }
   }

   /**
    * Executes a test taking care of invoking user level listeners.
    * The test is always marked as started at this stage.
    *
    * If the before-test listeners fail, then the test is not executed, but the after-test listeners
    * are executed, and the returned result contains the listener exception.
    *
    * If the test itself fails, then the after-test listeners are executed,
    * and the returned result is generated from the test exception.
    *
    * If the after-test listeners fail, then the returned result is taken from the listener exception
    * and any result from the test itself is ignored.
    *
    * Essentially, the after-test listeners are always attempted, and any error from invoking the before, test,
    * or after code is returned as higher priority than the result from the test case itself.
    */
   private suspend fun executeActiveTest(
      testCase: TestCase,
      context: TestContext,
      mark: TimeMark
   ): TestResult {

      log("Executing active test $testCase")
      listener.testStarted(testCase)

      return testCase
         .invokeAllBeforeTestCallbacks()
         .flatMap { invokeTestCase(executionContext, it, context, mark) }
         .fold(
            {
               TestResult.throwable(it, mark.elapsedNow()).apply {
                  testCase.invokeAllAfterTestCallbacks(this)
               }
            },
            { result ->
               testCase.invokeAllAfterTestCallbacks(result)
                  .fold(
                     { TestResult.throwable(it, mark.elapsedNow()) },
                     { result }
                  )
            }
         )
   }

   /**
    * Invokes the given [TestCase] on the given executor.
    */
   private suspend fun invokeTestCase(
      ec: TimeoutExecutionContext,
      testCase: TestCase,
      context: TestContext,
      mark: TimeMark
   ): Try<TestResult> = Try {
       log("invokeTestCase $testCase")

       if (testCase.config.invocations > 1 && testCase.type == TestType.Container)
           error("Cannot execute multiple invocations in parent tests")

       val t = executeAndWait(ec, testCase, context)

       val result = if (t == null) TestResult.success(mark.elapsedNow()) else TestResult.throwable(t, mark.elapsedNow())
       log("Test completed with result $result")
       result
   }

   /**
    * Invokes the given [TestCase] handling timeouts.
    * We create a scope here so that our coroutine waits for any child coroutines created by user code.
    */
   private suspend fun executeAndWait(
      ec: TimeoutExecutionContext,
      testCase: TestCase,
      context: TestContext
   ): Throwable? {

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      val timeout = testCase.config.resolvedTimeout()
      log("Test will execute with timeout $timeout")

      // this timeout applies to each inovation. If a test has invocations = 3, and this timeout
      // is set to 300ms, then each individual invocation must complete in under 300ms.
      val invocationTimeout = testCase.config.resolvedInvocationTimeout()
      log("Test will execute with invocationTimeout $invocationTimeout")

      return try {

         // all platforms support coroutine based interruption
         withTimeout(timeout.toLongMilliseconds()) {
            // not all platforms support executing with a timeout because it uses background threads to interrupt
            ec.executeWithTimeoutInterruption(timeout) {
               withTimeout(invocationTimeout.toLongMilliseconds()) {
                  replay(
                     testCase.config.invocations,
                     testCase.config.threads,
                     { testCase.invokeBeforeInvocation(it) },
                     { testCase.invokeAfterInvocation(it) }) {
                     coroutineScope {
                        val contextp = object : TestContext() {
                           override val testCase: TestCase = context.testCase
                           override suspend fun registerTestCase(nested: NestedTest) = context.registerTestCase(nested)
                           override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
                        }
                        testCase.executeWithBehaviours(contextp)
                     }
                  }
               }
            }
         }
         null
      } catch (e: TimeoutCancellationException) {
         log("Timeout exception $e")
         TimeoutException(timeout)
      } catch (t: Throwable) {
         t
      } catch (e: AssertionError) {
         e
      }
   }
}
