package io.kotlintest.internal

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.extensions.TopLevelTest

/**
 * Returns the top level [TestCase]s to run, in the order they
 * should be run.
 *
 * Takes into account focused tests, which can override
 * the active/inactive behavior defined in [isActive].
 */
fun topLevelTests(spec: Spec): List<TopLevelTest> {

  val order = spec.testCaseOrder() ?: Project.testCaseOrder()

  val tests = when (order) {
    TestCaseOrder.Sequential -> spec.testCases()
    TestCaseOrder.Random -> spec.testCases().shuffled()
  }

  val focused = tests.find { it.name.startsWith("f:") }

  // if we have no focused tests, then we default to the standard is active logic
  // otherwise focused overrides
  return if (focused == null) {
    tests.map { TopLevelTest(it, isActive(it)) }
  } else {
    listOf(focused).map { TopLevelTest(it, true) } + tests.minus(focused).map { TopLevelTest(it, false) }
  }
}