package io.kotest.engine.listener

import io.kotest.core.test.TestResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Wraps a [TestEngineListener]s methods with a mutex to ensure only one method is called at a time.
 */
class ThreadSafeTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

   private val mutex = Mutex()

   override suspend fun executionIgnored(node: Node, reason: String?) {
      mutex.withLock {
         listener.executionIgnored(node, reason)
      }
   }

   override suspend fun executionFinished(node: Node, result: TestResult) {
      mutex.withLock {
         listener.executionFinished(node, result)
      }
   }

   override suspend fun executionStarted(node: Node) {
      mutex.withLock {
         listener.executionStarted(node)
      }
   }
}
