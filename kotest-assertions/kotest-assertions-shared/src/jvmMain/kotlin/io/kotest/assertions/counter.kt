@file:JvmName("jvmcounter")

package io.kotest.assertions

import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

actual val assertionCounter: AssertionCounter get() = threadLocalAssertionCounter.get()

/**
 * A [CoroutineContext.Element] which keeps the [assertionCounter] synchronized with thread-switching coroutines.
 *
 * When using [assertionCounter] without the Kotest framework, this context element should be added to a
 * coroutine context, e.g. via
 * - `runBlocking(assertionCounterContextElement) { ... }`
 * - `runTest(Dispatchers.IO + assertionCounterContextElement) { ... }`
 */
val assertionCounterContextElement: CoroutineContext.Element
   get() = threadLocalAssertionCounter.asContextElement()

private val threadLocalAssertionCounter: ThreadLocal<CoroutineLocalAssertionCounter> =
   ThreadLocal.withInitial { CoroutineLocalAssertionCounter() }

private class CoroutineLocalAssertionCounter : AssertionCounter {
   private var value = 0

   override fun get(): Int = value

   override fun reset() {
      value = 0
   }

   override fun inc() {
      value++
   }
}
