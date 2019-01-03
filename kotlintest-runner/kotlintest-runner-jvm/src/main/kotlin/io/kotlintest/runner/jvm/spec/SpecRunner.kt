package io.kotlintest.runner.jvm.spec

import createSpecInterceptorChain
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestListener
import io.kotlintest.runner.jvm.TestEngineListener

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

  protected fun interceptSpec(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    executeBeforeSpec(spec, listeners)

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.description(), spec)
    val chain = createSpecInterceptorChain(context, extensions) { afterInterception() }
    chain.invoke()

    executeAfterSpec(spec, listeners)
  }

  private fun executeBeforeSpec(spec: Spec, listeners: List<TestListener>) {
    listeners.forEach {
      it.beforeSpec(spec.description(), spec)
    }
  }

  private fun executeAfterSpec(spec: Spec, listeners: List<TestListener>) {
    listeners.reversed().forEach { it.afterSpec(spec.description(), spec) }
  }


}