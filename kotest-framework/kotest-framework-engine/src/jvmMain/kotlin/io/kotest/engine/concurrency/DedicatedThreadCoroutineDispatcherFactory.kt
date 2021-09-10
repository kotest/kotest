package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * A [CoroutineDispatcherFactory] that creates a dedicated thread for each test case.
 * Once the test completes, the thread is stopped.
 */
object DedicatedThreadCoroutineDispatcherFactory : CoroutineDispatcherFactory {

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      val executor = Executors.newSingleThreadExecutor()
      return try {
         withContext(executor.asCoroutineDispatcher()) {
            f()
         }
      } finally {
         executor.shutdown()
      }
   }
}
