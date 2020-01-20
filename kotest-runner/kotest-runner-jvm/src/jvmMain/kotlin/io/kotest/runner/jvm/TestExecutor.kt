package io.kotest.runner.jvm

import io.kotest.core.config.Project
import io.kotest.core.executeWithAssertionsCheck
import io.kotest.core.executeWithGlobalAssertSoftlyCheck
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.extensions.TestListener
import io.kotest.core.internal.unwrapIfReflectionCall
import io.kotest.core.spec.resolvedExtensions
import io.kotest.core.spec.resolvedListeners
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.fp.recover
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
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

   private val logger = LoggerFactory.getLogger(this.javaClass)

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
      onResult: (TestResult) -> Unit
   ) {
      logger.trace("Evaluating $testCase")

      val start = System.currentTimeMillis()

      val extensions = testCase.config.extensions +
         testCase.spec.resolvedExtensions().filterIsInstance<TestCaseExtension>() +
         Project.extensions().filterIsInstance<TestCaseExtension>()

      runExtensions(testCase, context, start, notifyListener, extensions) {
         when (it.status) {
            TestStatus.Ignored -> if (notifyListener) listener.testIgnored(testCase, null)
            else -> if (notifyListener) listener.testFinished(testCase, it)
         }
         onResult(it)
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
      notifyListener: Boolean,
      remaining: List<TestCaseExtension>,
      onComplete: suspend (TestResult) -> Unit
   ) {
      when {
         remaining.isEmpty() -> {
            val result = executeIfActive(testCase, context, start, notifyListener)
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

   private suspend fun executeIfActive(
      testCase: TestCase,
      context: TestContext,
      start: Long,
      notifyListener: Boolean
   ): TestResult {

      val active = testCase.isActive()
      logger.trace("Test ${testCase.description.fullName()} active=$active")

      // if the test case is active we execute it, otherwise we just invoke the callback with ignored
      return when (active) {
         true -> executeActiveTest(testCase, context, start, notifyListener)
         false -> TestResult.Ignored
      }
   }

   private suspend fun executeActiveTest(
      testCase: TestCase,
      context: TestContext,
      start: Long,
      notifyListener: Boolean
   ): TestResult {
      logger.trace("Executing active test $testCase")
      if (notifyListener) listener.testStarted(testCase)

      return userBeforeTest(testCase)
         .flatMap {
            val result = invokeTestCase(testCase, context, start)
            userAfterTest(testCase, result)
         }.recover { TestResult.throwable(it, (System.currentTimeMillis() - start).milliseconds) }
   }

   private suspend fun invokeTestCase(testCase: TestCase, context: TestContext, start: Long): TestResult {
      logger.trace("invokeTestCase $testCase")

      // we calculate the timeout, (if timeout has been configured) which will fail the test with a timed-out status
      val timeout = testCase.config.resolvedTimeout().toLongMilliseconds()
      val hasResumed = AtomicBoolean(false)

      val error = suspendCoroutine<Throwable?> { cont ->

         logger.trace("Scheduler will interrupt this test in ${timeout}ms")
         scheduler.schedule({
            if (hasResumed.compareAndSet(false, true)) {
               val t = TimeoutException("Execution of test took longer than ${timeout}ms")
               executor.shutdownNow()
               cont.resumeWith(Result.success(t))
            }
         }, timeout, TimeUnit.MILLISECONDS)

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

                  // we ensure the timeout is honoured
                  withTimeout(timeout) {
                     // we only run the assertions check for leaf tests
                     when (testCase.type) {
                        TestType.Container -> executeWithGlobalAssertSoftlyCheck { testCase.test.invoke(contextp) }
                        TestType.Test -> testCase.spec.resolvedAssertionMode().executeWithAssertionsCheck(
                           { testCase.test.invoke(contextp) },
                           testCase.description.name
                        )
                     }
                  }
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
      logger.debug("Test completed with result $result")
      return result
   }

   /**
    * Notifies the user listeners that a [TestCase] is starting.
    * The listeners will not be invoked if the test case is disabled.
    */
   private suspend fun userBeforeTest(testCase: TestCase): Try<TestCase> = Try {
      logger.trace("Executing listeners beforeTest")
      suspendCoroutine<TestCase> { cont ->
         executor.submit {
            try {
               runBlocking {
                  val listeners = testCase.spec.resolvedListeners()
                  listeners.forEach {
                     it.beforeTest(testCase)
                  }
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
   private suspend fun userAfterTest(testCase: TestCase, result: TestResult): Try<TestResult> = Try {
      logger.trace("Executing listeners afterTest")

      suspend fun runListeners(): TestResult {
         val listeners = testCase.spec.resolvedListeners()
         listeners.forEach {
            it.afterTest(testCase, result)
         }
         return result
      }

      // if the executor has been shutdown (because it timed out) then we have to run the listeners
      // on the main thread (as the executor is out of action!)
      if (executor.isShutdown) runListeners() else {
         suspendCoroutine<TestResult> { cont ->
            executor.submit {
               try {
                  runBlocking {
                     runListeners()
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
