package io.kotest.runner.jvm.spec

import io.kotest.core.IsolationMode
import io.kotest.Project
import io.kotest.SpecClass
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.TestListener
import io.kotest.extensions.TopLevelTests
import io.kotest.runner.jvm.TestEngineListener

/**
 * The base class for executing all the tests inside a [SpecClass].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 * The implementation for each mode is handled by an instance of [SpecRunner].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(val listener: TestEngineListener) {

   abstract suspend fun execute(spec: SpecConfiguration, topLevelTests: TopLevelTests): Map<TestCase, TestResult>

   private suspend fun interceptSpec(
      spec: SpecConfiguration,
      remaining: List<SpecExtension>,
      afterInterception: suspend () -> Unit
   ) {
      // todo
      val listeners = Project.listeners() // listOf(spec) // + spec.listenerInstances + Project.listeners()
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

   private fun executeBeforeSpec(spec: SpecConfiguration, listeners: List<TestListener>) {
      listeners.forEach {
         it.beforeSpec(spec::class.description(), spec)
         it.beforeSpec(spec)
      }
   }

   private fun executeAfterSpec(spec: SpecConfiguration, listeners: List<TestListener>) {
      listeners.reversed().forEach {
         it.afterSpec(spec)
         it.afterSpec(spec::class.description(), spec)
      }
   }

   suspend fun interceptSpec(spec: SpecConfiguration, afterInterception: suspend () -> Unit) {
      val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
      interceptSpec(spec, extensions, afterInterception)
   }
}
