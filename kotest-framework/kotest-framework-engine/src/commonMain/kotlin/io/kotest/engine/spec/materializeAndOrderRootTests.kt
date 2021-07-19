package io.kotest.engine.spec

import io.kotest.core.execution.ExecutionContext
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCaseOrder

/**
 * Returns the materialized tests from the receiver sorted according to the applicable [TestCaseOrder].
 */
fun Spec.materializeAndOrderRootTests(context: ExecutionContext): List<RootTest> {
   val order = this.testCaseOrder() ?: this.testOrder ?: context.configuration.testCaseOrder
   val tests = materializeRootTests(context)
   return when (order) {
      TestCaseOrder.Sequential -> tests
      TestCaseOrder.Random -> tests.shuffled()
      TestCaseOrder.Lexicographic -> tests.sortedBy { it.testCase.descriptor.name.testName }
   }
}
