package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that wraps one or more other test engine listeners,
 * forwarding calls to all listeners.
 */
class CompositeTestEngineListener(private val listeners: List<TestEngineListener>) : TestEngineListener {

   init {
      require(listeners.isNotEmpty())
   }

   override suspend fun engineShutdown() {
      listeners.forEach { it.engineShutdown() }
   }

   override suspend fun engineStartup() {
      listeners.forEach { it.engineStartup() }
   }

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      listeners.forEach { it.engineStarted(classes) }
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      listeners.forEach { it.engineFinished(t) }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      listeners.forEach { it.specStarted(kclass) }
   }

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      listeners.forEach { it.specFinished(kclass, results) }
   }

   override suspend fun testStarted(testCase: TestCase) {
      listeners.forEach { it.testStarted(testCase) }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      listeners.forEach { it.testIgnored(testCase, reason) }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      listeners.forEach { it.testFinished(testCase, result) }
   }

   override suspend fun specInstantiated(spec: Spec) {
      listeners.forEach { it.specInstantiated(spec) }
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      listeners.forEach { it.specInstantiationError(kclass, t) }
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
      listeners.forEach { it.specExit(kclass, t) }
   }

   override suspend fun specIgnored(kclass: KClass<out Spec>) {
      listeners.forEach { it.specIgnored(kclass) }
   }

   override suspend fun specEnter(kclass: KClass<out Spec>) {
      listeners.forEach { it.specEnter(kclass) }
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      listeners.forEach { it.specInactive(kclass, results) }
   }
}
