package io.kotest.engine.test

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.TestTimeoutException
import io.kotest.engine.extensions.resolvedTestCaseExtensions
import io.kotest.engine.lifecycle.invokeAfterInvocation
import io.kotest.engine.lifecycle.invokeAllAfterTestCallbacks
import io.kotest.engine.lifecycle.invokeAllBeforeTestCallbacks
import io.kotest.engine.lifecycle.invokeBeforeInvocation
import io.kotest.engine.test.status.isEnabled
import io.kotest.fp.Try
import io.kotest.mpp.log
import io.kotest.mpp.replay
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.min

/**
 * Validates that a [TestCase] is compatible on the actual platform. For example, in JS we can only
 * support certain spec styles due to limitations in the underlying test runners.
 */
typealias ValidateTestCase = (TestCase) -> Unit

/**
 * Returns a [TestResult] for the given throwable and test execution duration.
 */
typealias ToTestResult = (Throwable?, Long) -> TestResult

/**
 * Executes a single [TestCase].
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test.
 *
 * The [TimeoutExecutionContext] is used to provide a way of executing functions on the underlying platform
 * in a way that best utilizes threads or the lack of on that platform.
 *
 * If the given test case fails to validate via [validateTestCase], then this method throws.
 */
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val executionContext: TimeoutExecutionContext,
   private val validateTestCase: ValidateTestCase,
   private val toTestResult: ToTestResult,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point [testCase=${testCase.displayName}, context=$context]" }
      validateTestCase(testCase)
      val start = timeInMillis()
      val extensions = testCase.resolvedTestCaseExtensions()
      return intercept(testCase, context, start, extensions).apply {
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
      start: Long,
      extensions: List<TestCaseExtension>,
   ): TestResult {

      val innerExecute: suspend (TestCase, TestContext) -> TestResult = { tc, ctx ->
         executeIfEnabled(tc) { executeActiveTest(tc, ctx, start) }
      }

      val execute = extensions.foldRight(innerExecute) { extension, execute ->
         { testCase, context ->
            extension.intercept(testCase) {
               // the user's intercept method is free to change the context of the coroutine
               // to support this, we should switch the context used by the test case context
               val newContext = context.withCoroutineContext(coroutineContext)
               execute(it, newContext)
            }
         }
      }

      return execute(testCase, context)
   }

   /**
    * Checks the enabled status of a [TestCase] before invoking it.
    * If the test is disabled, then [TestResult.ignored] is returned.
    */
   private suspend fun executeIfEnabled(testCase: TestCase, ifEnabled: suspend () -> TestResult): TestResult {
      // if the test case is active we execute it, otherwise we just invoke the callback with ignored
      val enabled = testCase.isEnabled()

      return when (enabled.isEnabled) {
         true -> {
            log { "${testCase.description.testPath()} is enabled" }
            ifEnabled()
         }
         false -> {
            log { "${testCase.description.testPath()} is disabled" }
            TestResult.ignored(enabled)
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
      start: Long,
   ): TestResult {

      log { "Executing active test $testCase with context $context" }
      listener.testStarted(testCase)

      return testCase
         .invokeAllBeforeTestCallbacks()
         .flatMap { invokeTestCase(executionContext, it, context, start) }
         .fold(
            {
               toTestResult(it, timeInMillis() - start).apply {
                  testCase.invokeAllAfterTestCallbacks(this)
               }
            },
            { result ->
               testCase.invokeAllAfterTestCallbacks(result)
                  .fold(
                     { toTestResult(it, timeInMillis() - start) },
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
      start: Long,
   ): Try<TestResult> = Try {
      log { "invokeTestCase $testCase" }

      if (testCase.config.invocations > 1 && testCase.type == TestType.Container)
         error("Cannot execute multiple invocations in parent tests")

      val t = executeAndWait(ec, testCase, context)
      log { "TestCaseExecutor: Test returned with error $t" }

      val result = toTestResult(t, timeInMillis() - start)
      log { "Test completed with result $result" }
      result
   }

   /**
    * Invokes the given [TestCase] handling timeouts.
    */
   private suspend fun executeAndWait(
      ec: TimeoutExecutionContext,
      testCase: TestCase,
      context: TestContext,
   ): Throwable? {

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      val timeout = testCase.resolvedTimeout()
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with timeout $timeout" }

      // this timeout applies to each inovation. If a test has invocations = 3, and this timeout
      // is set to 300ms, then each individual invocation must complete in under 300ms.
      // invocation timeouts are not applied to TestType.Container only TestType.Test
      val invocationTimeout = testCase.resolvedInvocationTimeout()
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with invocationTimeout $invocationTimeout" }

      // we don't want any errors in the test to propagate out and cancel all the coroutines used for
      // the specs / parent tests, therefore we install a supervisor job
      return supervisorScope {
         try {
            // all platforms support coroutine based interruption
            // this is the test level timeout
            withTimeout(timeout) {
               ec.executeWithTimeoutInterruption(timeout) {
                  // depending on the test type, we execute with an invocation timeout
                  when (testCase.type) {
                     TestType.Container -> executeInScope(testCase, context)
                     TestType.Test ->
                        // not all platforms support executing with an interruption based timeout
                        // because it uses background threads to interrupt
                        replay(
                           testCase.config.invocations,
                           testCase.config.threads,
                           { testCase.invokeBeforeInvocation(it) },
                           { testCase.invokeAfterInvocation(it) }) {
                           ec.executeWithTimeoutInterruption(invocationTimeout) {
                              withTimeout(invocationTimeout) {
                                 executeInScope(testCase, context)
                              }
                           }
                        }
                  }
               }
            }
            null
         } catch (e: TimeoutCancellationException) {
            log { "TestCaseExecutor: TimeoutCancellationException $e" }
            when (testCase.type) {
               TestType.Container -> TestTimeoutException(timeout, testCase.displayName)
               TestType.Test -> TestTimeoutException(min(timeout, invocationTimeout), testCase.displayName)
            }
         } catch (t: Throwable) {
            log { "TestCaseExecutor: Throwable $t" }
            t
         } catch (e: AssertionError) {
            log { "TestCaseExecutor: AssertionError $e" }
            e
         }
      }
   }

   /**
    * Execute the test case wrapped in a scope, so that we wait for any child coroutines created
    * by the user's test case.
    */
   private suspend fun executeInScope(testCase: TestCase, context: TestContext) = coroutineScope {
      val contextp = object : TestContext {
         override val testCase: TestCase = context.testCase
         override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
         override suspend fun registerTestCase(nested: NestedTest) = context.registerTestCase(nested)
      }
      testCase.executeWithBehaviours(contextp)
   }
}
