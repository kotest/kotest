package io.kotest.core.concurrency

import io.kotest.core.test.TestCase

/**
 * Switches the [kotlinx.coroutines.CoroutineDispatcher] used for test and spec execution.
 */
interface CoroutineDispatcherFactory {

   /**
    * Execute the given function [f] on a [kotlinx.coroutines.CoroutineDispatcher] chosen by this implementation.
    * It may be the same dispatcher as the calling coroutine.
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T
}
