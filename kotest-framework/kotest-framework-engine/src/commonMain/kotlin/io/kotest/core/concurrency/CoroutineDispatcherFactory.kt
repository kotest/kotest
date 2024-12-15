package io.kotest.core.concurrency

import io.kotest.core.test.TestCase
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Switches the [kotlinx.coroutines.CoroutineDispatcher] used for test and spec execution.
 */
interface CoroutineDispatcherFactory {

   /**
    * Execute the given function [f] on a [kotlinx.coroutines.CoroutineDispatcher] chosen by this implementation.
    * It may be the same dispatcher as the calling coroutine.
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T

   /**
    * Close dispatchers created by the factory, releasing resources.
    */
   fun close() {}
}

inline fun <T: CoroutineDispatcherFactory, R> T.use(block: (factory: T) -> R): R {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }
   return try {
      block(this).also {
         close()
      }
   } catch (e: Throwable) {
      try {
         close()
      } catch (_: Throwable) {
      }
      throw e
   }
}
