package io.kotest.engine.test.interceptors

import io.kotest.assertions.ThreadLocalAssertionCounter
import io.kotest.common.JVMOnly
import io.kotest.common.TestNameContextElement
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@JVMOnly
internal actual fun assertionModeThreadLocalContextInterceptor(): TestExecutionInterceptor =
   AssertionModeThreadLocalContextInterceptor

/**
 * Installs the [AssertionCounterThreadContextElement]s into the running coroutine context.
 */
object AssertionModeThreadLocalContextInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val testNameContextElement = coroutineContext[TestNameContextElement] ?: error("Requires TestNameContextElement")
      return withContext(AssertionCounterThreadContextElement(testNameContextElement.testName)) {
         test(testCase, scope.withCoroutineContext(this.coroutineContext))
      }
   }
}

private val testNameCounters = ConcurrentHashMap<String, Int>()

class AssertionCounterThreadContextElement(private val testName: String) : ThreadContextElement<Int> {

   companion object Key : CoroutineContext.Key<AssertionCounterThreadContextElement>

   override val key: CoroutineContext.Key<AssertionCounterThreadContextElement>
      get() = Key

   // this is invoked before coroutine is resumed on current thread
   override fun updateThreadContext(context: CoroutineContext): Int {
      // need to use our backing map's value and install that in the thread local copy
      val counter = testNameCounters.getOrPut(testName) { 0 }
      ThreadLocalAssertionCounter.values.set(counter)
      // we track the state in our backing map so this can be ignored
      return -1
   }

   // this is invoked after coroutine has suspended on current thread
   override fun restoreThreadContext(context: CoroutineContext, oldState: Int) {
      // need to put the current thread-local value into our backing map before the coroutine is switched out
      testNameCounters[testName] = ThreadLocalAssertionCounter.values.get()
      ThreadLocalAssertionCounter.values.set(oldState)
   }
}
