package io.kotest.engine.listener

import io.kotest.core.test.TestResult

/**
 * A [TestEngineListener] that wraps one or more other test engine listeners,
 * forwarding calls to all listeners.
 */
class CompositeTestEngineListener(private val listeners: List<TestEngineListener>) : TestEngineListener {

   init {
      require(listeners.isNotEmpty())
   }

   override suspend fun executionIgnored(node: Node, reason: String?) {
      listeners.forEach { it.executionIgnored(node, reason) }
   }

   override suspend fun executionStarted(node: Node) {
      listeners.forEach { it.executionStarted(node) }
   }

   override suspend fun executionFinished(node: Node, result: TestResult) {
      listeners.forEach { it.executionFinished(node, result) }
   }
}
