package io.kotest.engine.test.interceptors

import io.kotest.common.NonDeterministicTestVirtualTimeEnabled
import io.kotest.common.testCoroutineSchedulerOrNull
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.coroutines.TestScopeElement
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
internal class TestCoroutineInterceptor(private val testConfigResolver: TestConfigResolver) : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      val result = CompletableDeferred<TestResult>()
      logger.log { Pair(testCase.name.name, "Switching context to coroutines runTest") }

      // runTest starts a brand new coroutine, so nothing in the ambient coroutineContext survives
      // into it unless we forward it explicitly here. We forward everything except the elements
      // that would either break TestScope's own construction or unintentionally couple structured
      // concurrency with the caller:
      //  - Job: runTest manages its own root Job; forwarding the ambient one would parent it under
      //    an unrelated coroutine instead of leaving it independent, as before this change.
      //  - ContinuationInterceptor: TestScope requires this to be a TestDispatcher (or absent) --
      //    forwarding a real dispatcher installed by a CoroutineDispatcherFactory extension would
      //    throw during TestScope construction.
      //  - CoroutineExceptionHandler: TestScope requires this to be absent or an
      //    UncaughtExceptionCaptor -- forwarding an arbitrary ambient handler would also throw.
      // Everything else -- for example Spring's SpringTestContextCoroutineContextElement, installed via
      // withContext() around spec execution -- propagates through.
      val forwardedContext = currentCoroutineContext()
         .minusKey(Job)
         .minusKey(ContinuationInterceptor)
         .minusKey(CoroutineExceptionHandler)

      // Handle timeouts here to avoid the influence of the default timeout set inside runTest
      runTest(
         context = forwardedContext,
         timeout = testConfigResolver.timeout(testCase)
      ) {
         var additionalContext: CoroutineContext = TestScopeElement(this)
         if (testCase.spec.nonDeterministicTestVirtualTimeEnabled) {
            additionalContext += NonDeterministicTestVirtualTimeEnabled
         }
         withContext(additionalContext + KotlinTestRunTest) {
            try {
               result.complete(test(testCase, scope.withCoroutineContext(coroutineContext)))
            } catch (e: CancellationException) {
               result.cancel(e)
            } catch (e: Throwable) {
               result.completeExceptionally(e)
            }
         }
      }
      yield()
      return result.await()
   }
}

/**
 * We add this [CoroutineContext.Key] to the coroutine context so that we can detect when we are running
 * inside a [runTest] block.
 */
internal object KotlinTestRunTest : CoroutineContext.Key<KotlinTestRunTest>, CoroutineContext.Element {
   override val key: CoroutineContext.Key<*>
      get() = this

   override fun toString(): String = "KotlinTestRunTest"
}
