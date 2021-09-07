package io.kotest.engine.concurrency

import io.kotest.core.test.TestCase

expect val defaultCoroutineDispatcherProvider: CoroutineDispatcherAssignment

/**
 * Optionally switches execution of the given test or spec onto another dispatcher.
 */
interface CoroutineDispatcherAssignment {

   /**
    * Execute the given test function [f] on a dispatcher chosen by this implementation.
    * It may be the same dispatcher as the calling coroutine.
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T
}

object NoopCoroutineDispatcherAssignment : CoroutineDispatcherAssignment {
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return f()
   }
}
