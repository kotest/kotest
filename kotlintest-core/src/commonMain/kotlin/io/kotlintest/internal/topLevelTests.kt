package io.kotlintest.internal

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.listener.TopLevelTest
import io.kotlintest.listener.TopLevelTests

/**
 * Returns the top level [TestCase] instances from a Spec, ordered according
 * to the [TestCaseOrder] specified in the spec (or the project if not in the spec).
 * Captures information on focused tests.
 */
fun topLevelTests(spec: Spec): TopLevelTests {

  val order = spec.testCaseOrder() ?: Project.testCaseOrder()

  val tests = when (order) {
    TestCaseOrder.Sequential -> spec.testCases()
    TestCaseOrder.Random -> spec.testCases().shuffled()
  }

  return TopLevelTests(tests.withIndex().map { TopLevelTest(it.value, it.index) })
}