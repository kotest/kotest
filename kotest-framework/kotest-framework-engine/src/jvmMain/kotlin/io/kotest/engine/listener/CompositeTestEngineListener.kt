package io.kotest.engine.listener

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that wraps one or more other test engine listeners, forwarding all
 * calls to those listeners.
 */
class CompositeTestEngineListener(private val listeners: List<TestEngineListener>) : TestEngineListener {

   init {
      require(listeners.isNotEmpty())
   }

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      listeners.forEach { it.engineStarted(classes) }
   }

   override fun engineFinished(t: List<Throwable>) {
      listeners.forEach { it.engineFinished(t) }
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      listeners.forEach { it.specStarted(kclass) }
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      listeners.forEach { it.specFinished(kclass, t, results) }
   }

   override suspend fun specStarted(spec: Descriptor.SpecDescriptor) {
      listeners.forEach { it.specStarted(spec) }
   }

   override suspend fun specFinished(
      spec: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
      listeners.forEach { it.specFinished(spec, t, results) }
   }

   override fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
      listeners.forEach { it.testFinished(descriptor, result) }
   }

   override fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
      listeners.forEach { it.testIgnored(descriptor, reason) }
   }

   override fun testStarted(descriptor: Descriptor.TestDescriptor) {
      listeners.forEach { it.testStarted(descriptor) }
   }

   override fun testStarted(testCase: TestCase) {
      listeners.forEach { it.testStarted(testCase) }
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      listeners.forEach { it.testIgnored(testCase, reason) }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      listeners.forEach { it.testFinished(testCase, result) }
   }

   override fun specInstantiated(spec: Spec) {
      listeners.forEach { it.specInstantiated(spec) }
   }

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      listeners.forEach { it.specInstantiationError(kclass, t) }
   }
}
