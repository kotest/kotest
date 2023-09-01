package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

/**
 * A [CoroutineDispatcherFactory] that creates a dedicated thread for each test case.
 * Once the test completes, the thread is shutdown.
 */
object DedicatedThreadCoroutineDispatcherFactory : CoroutineDispatcherFactory {

   @OptIn(DelicateCoroutinesApi::class)
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T =
      newSingleThreadContext("dedicated").use { dispatcher ->
         withContext(dispatcher) {
            f()
         }
      }
}
