package io.kotest.runner.jvm

import io.kotest.assertions.log
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.internal.unwrapIfReflectionCall
import io.kotest.core.runtime.executeWithTimeout
import io.kotest.core.runtime.extensions
import io.kotest.core.runtime.invokeAfterTest
import io.kotest.core.runtime.invokeBeforeTest
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.fp.recover
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine
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
 * This class is not thread safe!
 */
@UseExperimental(ExperimentalTime::class)
class TestExecutor(private val listener: TestEngineListener) {

   // we run the entire test inside its own executor so that the before/after callbacks
   // and the test itself run on the same thread.
   // @see https://github.com/kotlintest/kotlintest/issues/447
   // this cannot be the main thread because we need to continue after a timeout, and
   // we can't interrupt a test doing `while (true) {}`
   private val executor = Executors.newSingleThreadExecutor()

   private val scheduler = Executors.newScheduledThreadPool(1)

   /**
    * Executes the given [TestCase] using the supplied [TestContext] and invoking [onResult]
    * with the outcome of the test.
    *
    * @param notifyListener if true, then will notify the [TestEngineListener] about lifecycle
    * events for this test. Note that user listeners will always be notified.
    */
   suspend fun execute(
      testCase: TestCase,
      context: TestContext,
      notifyListener: Boolean,
      onResult: suspend (TestResult) -> Unit
   ) {

      log("Evaluating $testCase")
      val start = System.currentTimeMillis()

      val onComplete: suspend (TestResult) -> Unit = {
         when (it.status) {
            TestStatus.Ignored -> if (notifyListener) listener.testIgnored(testCase, null)
            else -> if (notifyListener) listener.testFinished(testCase, it)
         }
         onResult(it)
      }

      runExtensions(testCase, context, start, notifyListener, testCase.extensions(), onComplete)
   }

   /**
    * Recursively runs the extensions until no extensions are left.
    * Each extension must invoke the callback given to it, or the test would hang.
    */
   private suspend fun runExtensions(
      testCase: TestCase,
      context: TestContext,
      start: Long,
      notifyListener: Boolean,
      remaining: List<TestCaseExtension>,
      onComplete: suspend (TestResult) -> Unit
   ) {
      when {
         remaining.isEmpty() -> {
            val result = executeIfActive(testCase) { executeActiveTest(testCase, context, start, notifyListener) }
            onComplete(result)
         }
         else -> {
            remaining.first().intercept(
               testCase,
               { test, callback -> runExtensions(test, context, start, notifyListener, remaining.drop(1), callback) },
               { onComplete(it) }
            )
         }
      }
   }


   /**
    * Checks the active status of a [TestCase] before invoking it.
    * If the test is inactive, then [TestResult.ignored] is returned.
    */
   private suspend fun executeIfActive(testCase: TestCase, ifActive: suspend () -> TestResult): TestResult {
      // if the test case is active we execute it, otherwise we just invoke the callback with ignored
      return when (testCase.isActive()) {
         true -> ifActive()
         false -> TestResult.Ignored
      }
   }

   private suspend fun executeActiveTest(
      testCase: TestCase,
      context: TestContext,
      start: Long,
      notifyListener: Boolean
   ): TestResult {
      log("Executing active test $testCase")
      if (notifyListener) listener.testStarted(testCase)

      return beforeTestListeners(testCase)
         .flatMap {
            val result = invokeTestCase(testCase, context, start)
            afterTestListeners(testCase, result)
         }.recover { TestResult.throwable(it, (System.currentTimeMillis() - start).milliseconds) }
   }

   private suspend fun invokeTestCase(testCase: TestCase, context: TestContext, start: Long): TestResult {
      log("invokeTestCase $testCase")

      // we calculate the timeout, (if timeout has been configured) which will fail the test with a timed-out status
      val timeout = testCase.config.resolvedTimeout()
      val hasResumed = AtomicBoolean(false)

      val error = suspendCoroutine<Throwable?> { cont ->

         log("Scheduler will interrupt this test in ${timeout}ms")
         scheduler.schedule({
            if (hasResumed.compareAndSet(false, true)) {
               val t = TimeoutException("Execution of test took longer than ${timeout}ms")
               executor.shutdownNow()
               cont.resumeWith(Result.success(t))
            }
         }, timeout.toLongMilliseconds(), TimeUnit.MILLISECONDS)

         // the test is running inside this executor so we can wait outside for it, but only up to the timeout
         // value. Without this executor we have no way of interrupting a test that isn't cooperative.
         executor.submit {
            try {

               runBlocking {
                  // we need to wrap the test context in one that extends the outer scope otherwise
                  // launched coroutines will take place on the test executors own coroutine
                  val contextp = object : TestContext() {
                     override val testCase: TestCase = context.testCase
                     override suspend fun registerTestCase(nested: NestedTest) = context.registerTestCase(nested)
                     override val coroutineContext: CoroutineContext = this@runBlocking.coroutineContext
                  }
                  testCase.executeWithTimeout(contextp, timeout)
               }

               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWith(Result.success(null))

            } catch (e: TimeoutCancellationException) {
               val t = TimeoutException("Execution of test took longer than ${timeout}ms")
               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWith(Result.success(t))
            } catch (e: Throwable) {
               val ep = e.unwrapIfReflectionCall()
               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWith(Result.success(ep))
            } catch (e: AssertionError) {
               if (hasResumed.compareAndSet(false, true))
                  cont.resumeWith(Result.success(e))
            }
         }
      }

      val result = TestResult.throwable(error, (System.currentTimeMillis() - start).milliseconds)
      log("Test completed with result $result")
      return result
   }

   /**
    * Notifies the user listeners that a [TestCase] is starting.
    * The listeners will not be invoked if the test case is disabled.
    */
   private suspend fun beforeTestListeners(testCase: TestCase): Try<TestCase> = Try {
      log("Executing listeners beforeTest")
      suspendCoroutine<TestCase> { cont ->
         executor.submit {
            try {
               runBlocking {
                  testCase.invokeBeforeTest()
               }
               cont.resumeWith(Result.success(testCase))
            } catch (e: Throwable) {
               cont.resumeWith(Result.failure(e))
            }
         }
      }
   }

   /**
    * Notifies the user listeners that a [TestCase] has finished.
    * The listeners will not be invoked if the test case is disabled.
    */
   private suspend fun afterTestListeners(testCase: TestCase, result: TestResult): Try<TestResult> = Try {
      log("Executing listeners afterTest")

      // if the executor has been shutdown (because it timed out) then we have to run the listeners
      // on the main thread (as the executor is out of action!)
      if (executor.isShutdown) {
         testCase.invokeAfterTest(result)
         result
      } else {
         suspendCoroutine<TestResult> { cont ->
            executor.submit {
               try {
                  runBlocking {
                     testCase.invokeAfterTest(result)
                     result
                  }
                  cont.resumeWith(Result.success(result))
               } catch (e: Throwable) {
                  cont.resumeWith(Result.failure(e))
               }
            }
         }
      }
   }
}
