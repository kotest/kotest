package io.kotest.engine.coroutines

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Switches the [kotlinx.coroutines.CoroutineDispatcher] used for spec and test execution.
 */
interface CoroutineDispatcherFactory {

   /**
    * Switches the [kotlinx.coroutines.CoroutineDispatcher] used for spec callbacks
    * such as beforeSpec and afterSpec.
    */
   suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T

   /**
    * Execute the given function [f] on a  chosen by this implementation.
    * It may be the same dispatcher as the calling coroutine.
    *
    * Note, this method is not invoked if the test case is already executing inside a context
    * that is a [kotlinx.coroutines.test.TestDispatcher].
    */
   suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T

   /**
    * Closes this factory, releasing resources if required.
    *
    * This method is invoked after the test engine has completed executing all specs.
    */
   fun close() {}
}

inline fun <T : CoroutineDispatcherFactory, R> T.use(block: (factory: T) -> R): R {
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
