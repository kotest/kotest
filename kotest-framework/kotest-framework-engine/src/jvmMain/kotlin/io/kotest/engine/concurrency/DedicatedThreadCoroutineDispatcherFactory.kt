package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * A [CoroutineDispatcherFactory] that creates a dedicated thread for each test case.
 * Once the test completes, the thread is shutdown.
 */
object DedicatedThreadCoroutineDispatcherFactory : CoroutineDispatcherFactory {

   @OptIn(DelicateCoroutinesApi::class)
   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T =
      Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
         withContext(dispatcher) {
            f()
         }
      }
}
