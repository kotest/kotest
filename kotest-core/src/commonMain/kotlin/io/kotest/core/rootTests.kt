package io.kotest.core

import io.kotest.core.specs.Spec
import io.kotest.extensions.TopLevelTest
import io.kotest.extensions.RootTests

/**
 * Returns the root [TestCase] instances from a Spec, ordered according
 * to the [TestCaseOrder] specified in the spec (or the project if not in the spec).
 * Captures information on focused tests.
 */
fun Spec.orderedRootTests(): RootTests {

   val tests = when (testCaseOrder ?: defaultTestCaseOrder()) {
      TestCaseOrder.Sequential -> this.tests
      TestCaseOrder.Random -> this.tests.shuffled()
   }

   return RootTests(tests.withIndex().map { TopLevelTest(it.value, it.index) })
}
