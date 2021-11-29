package io.kotest.engine.listener

import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExecutorDelegate
import io.kotest.mpp.Logger

object LoggingTestEngineListener : TestEngineListener {

   private val logger = Logger(SpecExecutorDelegate::class)

   override suspend fun executionIgnored(node: Node, reason: String?) {
      logger.log { Pair(null, "executionIgnored $node $reason") }
   }

   override suspend fun executionStarted(node: Node) {
      logger.log { Pair(null, "executionStarted $node") }
   }

   override suspend fun executionFinished(node: Node, result: TestResult) {
      logger.log { Pair(null, "executionFinished $node $result") }
   }
}
