package io.kotest.core.runtime

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Allows for the execution of a function with a timeout to wake blocked threads.
 */
@OptIn(ExperimentalTime::class)
interface TimeoutExecutionContext {
   suspend fun <T> executeWithTimeoutInterruption(timeout: Duration, f: suspend () -> T): T
}

/**
 * Implementation of [TimeoutExecutionContext] to be used in environments which do not provide
 * the ability to spawn threads, such as Javascript. All executions occur on the same thread
 * as the caller. This means we cannot detect a deadlock in a test as we can
 * on the JVM by running the test in a seperate thread.
 */
@OptIn(ExperimentalTime::class)
object CallingThreadExecutionContext : TimeoutExecutionContext {
   override suspend fun <T> executeWithTimeoutInterruption(timeout: Duration, f: suspend () -> T): T = f()
}
