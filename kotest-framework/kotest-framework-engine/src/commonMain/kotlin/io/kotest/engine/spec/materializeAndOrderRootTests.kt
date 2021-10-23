package io.kotest.engine.spec

import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCaseOrder

/**
 * Returns the materialized tests from the receiver [Spec] sorted according
 * to the applicable [TestCaseOrder].
 *
 * @param defaultTestCaseOrder the [TestCaseOrder] to use if the spec does not specify.
 */
fun Spec.materializeAndOrderRootTests(defaultTestCaseOrder: TestCaseOrder): List<RootTest> {
   val tests = materializeRootTests()
   return when (this.testCaseOrder() ?: this.testOrder ?: defaultTestCaseOrder) {
      TestCaseOrder.Sequential -> tests
      TestCaseOrder.Random -> tests.shuffled()
      TestCaseOrder.Lexicographic -> tests.sortedBy { it.testCase.name.testName }
   }
}
