package io.kotlintest.runner.jvm.spec

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.runner.jvm.TestEngineListener

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 * The implementation for each mode is handled by an instance of [SpecRunner].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(val listener: TestEngineListener) {

  abstract fun execute(spec: Spec)

  /**
   * Returns the top level [TestCase]s to run, in the order they
   * should be run. Takes into account focused tests.
   */
  fun topLevelTests(spec: Spec): List<TestCase> {
    val order = spec.testCaseOrder() ?: Project.testCaseOrder()
    val tests = when (order) {
      TestCaseOrder.Sequential -> spec.testCases()
      TestCaseOrder.Random -> spec.testCases().shuffled()
    }
    val focused = tests.find { it.name.startsWith("f:") }
    return if (focused == null) tests else listOf(focused)
  }

  private suspend fun interceptSpec(spec: Spec, remaining: List<SpecExtension>, afterInterception: suspend () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach { it.beforeSpec(spec.description(), spec) }

    when {
      remaining.isEmpty() -> {
        afterInterception()
        listeners.reversed().forEach { it.afterSpec(spec.description(), spec) }
      }
      else -> {
        val rest = remaining.drop(1)
        remaining.first().intercept(spec) { interceptSpec(spec, rest, afterInterception) }
      }
    }
  }

  suspend fun interceptSpec(spec: Spec, afterInterception: suspend () -> Unit) {
    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    interceptSpec(spec, extensions, afterInterception)
  }
}