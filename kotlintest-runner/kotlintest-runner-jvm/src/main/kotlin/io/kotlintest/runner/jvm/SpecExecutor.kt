package io.kotlintest.runner.jvm

import io.kotlintest.TestIsolationMode
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.internal.isActive
import io.kotlintest.runner.jvm.spec.InstancePerLeafSpecRunner
import io.kotlintest.runner.jvm.spec.InstancePerNodeSpecRunner
import io.kotlintest.runner.jvm.spec.SharedInstanceSpecRunner
import io.kotlintest.runner.jvm.spec.SpecRunner

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [TestIsolationMode] of the spec.
 */
class SpecExecutor(val listener: TestEngineListener) {

  fun execute(spec: Spec) {
    val (active, inactive) = topLevelTests(spec)
    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach { it.prepareSpec(spec, active, inactive) }
    val results = runner(spec).execute(spec, active, inactive)
    listeners.forEach { it.cleanupSpec(spec, results) }
  }

  /**
   * Returns the top level [TestCase]s to run, in the order they
   * should be run.
   *
   * Takes into account focused tests, which can override
   * the active/inactive behavior defined in [isActive].
   */
  fun topLevelTests(spec: Spec): Pair<List<TestCase>, List<TestCase>> {

    val order = spec.testCaseOrder() ?: Project.testCaseOrder()

    val tests = when (order) {
      TestCaseOrder.Sequential -> spec.testCases()
      TestCaseOrder.Random -> spec.testCases().shuffled()
    }

    val focused = tests.find { it.name.startsWith("f:") }

    return if (focused == null) {
      tests.partition { isActive(it) }
    } else {
      Pair(listOf(focused), tests.minus(focused))
    }
  }

  // returns a correct runner based on the isolation mode of the spec
  private fun runner(spec: Spec): SpecRunner {
    return when (spec.isolationMode()) {
      TestIsolationMode.SingleInstance -> SharedInstanceSpecRunner(listener)
      TestIsolationMode.InstancePerTest -> InstancePerNodeSpecRunner(listener)
      TestIsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(listener)
      null -> when {
        spec.isInstancePerTest() -> InstancePerNodeSpecRunner(listener)
        else -> SharedInstanceSpecRunner(listener)
      }
    }
  }
}
