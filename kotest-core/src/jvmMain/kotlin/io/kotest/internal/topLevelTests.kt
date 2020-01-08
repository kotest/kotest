package io.kotest.internal

import io.kotest.Project
import io.kotest.core.TestCase
import io.kotest.core.TestCaseOrder
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.TopLevelTest
import io.kotest.extensions.TopLevelTests

/**
 * Returns the top level [TestCase] instances from a Spec, ordered according
 * to the [TestCaseOrder] specified in the spec (or the project if not in the spec).
 * Captures information on focused tests.
 */
fun orderedRootTests(spec: SpecConfiguration): TopLevelTests {

  val order = spec.testCaseOrder() ?: Project.testCaseOrder()

  val tests = when (order) {
    TestCaseOrder.Sequential -> spec.testCases()
    TestCaseOrder.Random -> spec.testCases().shuffled()
  }

  return TopLevelTests(tests.withIndex().map { TopLevelTest(it.value, it.index) })
}
