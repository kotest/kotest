package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase

/**
 * A [CoroutineDispatcherFactory] that continues execution on the calling dispatcher.
 */
object NoopCoroutineDispatcherFactory : CoroutineDispatcherFactory {
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return f()
   }
}
