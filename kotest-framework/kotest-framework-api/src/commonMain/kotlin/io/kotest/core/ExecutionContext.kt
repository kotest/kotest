package io.kotest.core

import kotlinx.coroutines.withTimeout

/**
 * Allows for the execution of functions on a consistent thread, with or without timeout interruption.
 *
 * On the JVM, with multiple threads, we want to ensure tests and their callbacks are on the same thread,
 * so, for example, thread locals in beforeTest/afterTest can be used in the associate test.
 *
 * In addition, the JVM can use additional threads to interrupt deadlocked tests.
 *
 * On JS, which is single threaded all functions would operate on the calling thread anyway.
 * See [CallingThreadExecutionContext].
 *
 */
interface ExecutionContext {
   suspend fun <T> execute(f: suspend () -> T)
   suspend fun <T> executeWithTimeout(timeoutInMillis: Long, f: suspend () -> T)
}

/**
 * Implementation of [ExecutionContext] to be used in environments which do not provide
 * the ability to spawn threads, such as Javascript. All executions occur on the same thread
 * as the caller. This means we cannot detect a deadlock in a test as we can
 * on the JVM by running the test in a seperate thread.
 */
object CallingThreadExecutionContext : ExecutionContext {

   override suspend fun <T> execute(f: suspend () -> T) {
      f()
   }

   override suspend fun <T> executeWithTimeout(timeoutInMillis: Long, f: suspend () -> T) {
      withTimeout(timeoutInMillis) {
         f()
      }
   }
}
