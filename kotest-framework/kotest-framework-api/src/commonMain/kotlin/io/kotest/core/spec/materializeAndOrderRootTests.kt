package io.kotest.core.spec

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCaseOrder

/**
 * Returns the materialized tests from the receiver sorted according to the applicable [TestCaseOrder].
 */
fun Spec.materializeAndOrderRootTests(): List<RootTest> {
   val order = this.testCaseOrder() ?: this.testOrder ?: configuration.testCaseOrder
   val tests = materializeRootTests()
   return when (order) {
      TestCaseOrder.Sequential -> tests
      TestCaseOrder.Random -> tests.shuffled()
      TestCaseOrder.Lexicographic -> tests.sortedBy { it.testCase.description.displayName().toLowerCase() }
   }
}
