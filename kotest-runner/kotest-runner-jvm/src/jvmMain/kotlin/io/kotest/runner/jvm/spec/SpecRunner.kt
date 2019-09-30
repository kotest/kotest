package io.kotest.runner.jvm.spec

import io.kotest.IsolationMode
import io.kotest.Project
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.TestListener
import io.kotest.extensions.TopLevelTests
import io.kotest.listenerInstances
import io.kotest.runner.jvm.TestEngineListener

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

  abstract fun execute(spec: Spec, topLevelTests: TopLevelTests): Map<TestCase, TestResult>

  private suspend fun interceptSpec(spec: Spec, remaining: List<SpecExtension>, afterInterception: suspend () -> Unit) {
    val listeners = listOf(spec) + spec.listenerInstances + Project.listeners()
    when {
      remaining.isEmpty() -> {
        executeBeforeSpec(spec, listeners)
        afterInterception()
        executeAfterSpec(spec, listeners)
      }
      else -> {
        val rest = remaining.drop(1)
        remaining.first().intercept(spec) { interceptSpec(spec, rest, afterInterception) }
      }
    }
  }

  private fun executeBeforeSpec(spec: Spec, listeners: List<TestListener>) {
    listeners.forEach {
      it.beforeSpec(spec.description(), spec)
      it.beforeSpec(spec)
    }
  }

  private fun executeAfterSpec(spec: Spec, listeners: List<TestListener>) {
    listeners.reversed().forEach {
      it.afterSpec(spec)
      it.afterSpec(spec.description(), spec)
    }
  }

  suspend fun interceptSpec(spec: Spec, afterInterception: suspend () -> Unit) {
    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    interceptSpec(spec, extensions, afterInterception)
  }
}