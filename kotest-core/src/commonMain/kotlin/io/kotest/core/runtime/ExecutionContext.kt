package io.kotest.core.runtime

import io.kotest.fp.Try
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Abstracts the execution of functions. To be used when we want to ensure that multiple functions
 * are executed in the same thread, but we cannot control the threading directly in the common lib.
 */
@OptIn(ExperimentalTime::class)
interface ExecutionContext {
   suspend fun <T> execute(f: suspend () -> T): Try<T>
   suspend fun <T> executeWithTimeoutInterruption(timeout: Duration, f: suspend () -> T): T
   fun close()
}

/**
 * Implementation of [ExecutionContext] to be used in environments which do not provide
 * the ability to spawn threads, such as Javascript. All executions occur on the same thread
 * as the caller. This means we cannot detect a deadlock in a test as we can
 * on the JVM by running the test in a seperate thread.
 */
@OptIn(ExperimentalTime::class)
object CallingThreadExecutionContext : ExecutionContext {
   override suspend fun <T> execute(f: suspend () -> T): Try<T> = Try { f() }
   override suspend fun <T> executeWithTimeoutInterruption(timeout: Duration, f: suspend () -> T): T = f()
   override fun close() {}
}
