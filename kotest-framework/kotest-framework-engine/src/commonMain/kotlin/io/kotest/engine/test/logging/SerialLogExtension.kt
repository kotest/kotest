package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [SerialLogExtension] wraps a [LogExtension] with a mutex, so we can guarantee
 * that calls to [LogExtension.handleLogs] are invoked sequentially.
 */
@ExperimentalKotest
internal class SerialLogExtension(private val logExtension: LogExtension) {
   private val mutex = Mutex()

   suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) = mutex.withLock {
      runCatching {
         logExtension.handleLogs(testCase, logs)
      }
   }
}
