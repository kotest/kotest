package io.kotest.engine.concurrency

import io.kotest.core.test.TestCase
import kotlinx.coroutines.CoroutineDispatcher

expect val defaultCoroutineDispatcherController: CoroutineDispatcherController

/**
 * Optionally switches execution of the given test or spec onto a [CoroutineDispatcher].
 */
interface CoroutineDispatcherController {

   /**
    * Execute the given function [f] on a [CoroutineDispatcher] chosen by this implementation.
    * It may be the same dispatcher as the calling coroutine.
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T
}

object NoopCoroutineDispatcherController : CoroutineDispatcherController {
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return f()
   }
}
