package io.kotest.runner.jvm.spec

import io.kotest.core.IsolationMode
import io.kotest.core.TestResult
import io.kotest.core.Project
import io.kotest.core.TestCase
import io.kotest.core.specs.Spec
import io.kotest.extensions.RootTests
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.TestListener
import io.kotest.runner.jvm.TestEngineListener

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 * The implementation for each mode is handled by an instance of [SpecRunner].
 *
 * @param listener provides callbacks on test state, used to feed back into the client
 * such as JUnit Platform or intellJ
 */
abstract class SpecRunner(val listener: TestEngineListener) {

   abstract fun execute(spec: Spec, rootTests: RootTests): Map<TestCase, TestResult>

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecExtension>,
      afterInterception: suspend () -> Unit
   ) {
      val listeners = spec.listeners + Project.listeners()
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

   // todo restore these listeners
   private fun executeBeforeSpec(spec: Spec, listeners: List<TestListener>) {
      listeners.forEach {
         //  it.beforeSpec(spec.description(), spec)
         // it.beforeSpec(spec)
      }
   }

   // todo restore these listeners
   private fun executeAfterSpec(spec: Spec, listeners: List<TestListener>) {
      listeners.reversed().forEach {
         //   it.afterSpec(spec)
         //   it.afterSpec(spec.description(), spec)
      }
   }

   suspend fun interceptSpec(spec: Spec, afterInterception: suspend () -> Unit) {
      val extensions = spec.extensions.filterIsInstance<SpecExtension>() + Project.specExtensions()
      interceptSpec(spec, extensions, afterInterception)
   }
}
