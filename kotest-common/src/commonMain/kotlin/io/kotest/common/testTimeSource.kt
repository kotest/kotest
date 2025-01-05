package io.kotest.common

import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.TimeSource

/**
 * Returns the [TimeSource] used in tests.
 *
 * This is [TimeSource.Monotonic] or virtual time, depending on the scheduler in use.
 */
@OptIn(KotestInternal::class)
suspend fun testTimeSource(): TimeSource =
   coroutineContext.testCoroutineSchedulerOrNull?.timeSource ?: TimeSource.Monotonic

@KotestInternal
val CoroutineContext.testCoroutineSchedulerOrNull: TestCoroutineScheduler?
   get() = when (val dispatcher = this[ContinuationInterceptor]) {
      is TestDispatcher -> dispatcher.scheduler
      else -> null
   }

/**
 * Returns the [TimeSource] used for non-deterministic test functions.
 *
 * This is [TimeSource.Monotonic] or – if separately enabled – virtual time, depending on the scheduler in use.
 */
@KotestInternal
suspend fun nonDeterministicTestTimeSource(): TimeSource =
   coroutineContext.nonDeterministicTestCoroutineSchedulerOrNull?.timeSource ?: TimeSource.Monotonic

private val CoroutineContext.nonDeterministicTestCoroutineSchedulerOrNull: TestCoroutineScheduler?
   get() = if (this[NonDeterministicTestVirtualTimeEnabled] != null) testCoroutineSchedulerOrNull else null

/**
 * A coroutine context key denoting that virtual time is enabled in non-deterministic test functions.
 */
@KotestInternal
object NonDeterministicTestVirtualTimeEnabled :
   CoroutineContext.Key<NonDeterministicTestVirtualTimeEnabled>, CoroutineContext.Element {

   override val key: CoroutineContext.Key<*>
      get() = this

   override fun toString(): String = "NonDeterministicTestVirtualTimeEnabled"
}
