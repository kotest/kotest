package io.kotest.engine.coroutines

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * A [CoroutineDispatcherFactory] that spins up a dedicated thread per spec, and creates a [CoroutineDispatcher]
 * from that. This dispatcher is then used as the context for that spec and all tests contained within.
 * The thread is shutdown when the spec completes.
 */
object ThreadPerSpecCoroutineContextFactory : CoroutineDispatcherFactory {

   override suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T {
      return Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
         withContext(dispatcher) {
            f()
         }
      }
   }

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      // nothing to do, we've already switched dispatcher in the spec variant
      return f()
   }

   override fun close() {
      // nothing to do here as each thread is shutdown after the spec completes
   }
}
