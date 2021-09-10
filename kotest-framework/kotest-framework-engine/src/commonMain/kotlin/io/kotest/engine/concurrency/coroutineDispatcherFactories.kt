package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase

/**
 * Returns the default [CoroutineDispatcherFactory] used unless overriden in configuration
 * or per spec.
 */
expect fun defaultCoroutineDispatcherFactory(): CoroutineDispatcherFactory

/**
 * A [CoroutineDispatcherFactory] that continues execution on the calling dispatcher.
 */
object NoopCoroutineDispatcherFactory : CoroutineDispatcherFactory {
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return f()
   }
}
