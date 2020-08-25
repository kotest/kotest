package io.kotest.core

/**
 * Allows for the execution of a function with a timeout to wake blocked threads.
 */
interface TimeoutExecutionContext {
   suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T
}

/**
 * Implementation of [TimeoutExecutionContext] to be used in environments which do not provide
 * the ability to spawn threads, such as Javascript. All executions occur on the same thread
 * as the caller. This means we cannot detect a deadlock in a test as we can
 * on the JVM by running the test in a seperate thread.
 */
object CallingThreadExecutionContext : TimeoutExecutionContext {
   override suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T = f()
}
