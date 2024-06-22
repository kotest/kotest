package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.coroutineContext
import kotlin.time.TimeSource
import kotlin.coroutines.CoroutineContext

/**
 * A [CoroutineContext.Element] governing the [TimeSource] used in tests.
 */
@KotestInternal
class TestTimeSourceContextElement(
   internal val timeSource: TimeSource
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestTimeSourceContextElement>
}

/**
 * Returns the [TimeSource] used in tests.
 *
 * This is [TimeSource.Monotonic] or virtual time, depending on the scheduler in use.
 */
@OptIn(KotestInternal::class)
suspend fun testTimeSource(): TimeSource =
   coroutineContext[TestTimeSourceContextElement.Key]?.timeSource ?: TimeSource.Monotonic
