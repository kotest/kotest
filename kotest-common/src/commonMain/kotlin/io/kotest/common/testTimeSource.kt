package io.kotest.common

import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.coroutineContext
import kotlin.time.TimeSource
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

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
