package io.kotest.engine.test

/**
 * Allows for the execution of a function with the ability to interupt the thread.
 */
interface InterruptableExecutionContext {
   suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T
}

/**
 * Implementation of [InterruptableExecutionContext] to be used in environments which do not provide
 * the ability to spawn and interrupt threads, such as Javascript. All executions occur on the same thread
 * as the caller. This means we cannot detect a deadlock in a test as we can
 * on the JVM by running the test in a seperate thread and interrupting.
 */
object NoInterruptionExecutionContext : InterruptableExecutionContext {
   override suspend fun <T> executeWithTimeoutInterruption(timeoutInMillis: Long, f: suspend () -> T): T = f()
}
