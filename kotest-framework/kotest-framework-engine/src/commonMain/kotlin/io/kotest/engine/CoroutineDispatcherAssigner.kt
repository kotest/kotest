package io.kotest.engine

import io.kotest.core.test.TestCase

expect val defaultCoroutineDispatcherProvider: CoroutineDispatcherAssigner

interface CoroutineDispatcherAssigner {
   /**
    * Execute the given function [f] on a dispatcher chosen by this assigner.
    * It may be the same dispatcher as the calling coroutine.
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T
}

object NoopCoroutineDispatcherAssigner : CoroutineDispatcherAssigner {
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return f()
   }
}
