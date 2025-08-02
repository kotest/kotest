package io.kotest.engine.listener

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.TestResult
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that wraps one or more other test engine listeners,
 * forwarding calls to all listeners.
 */
class CompositeTestEngineListener(private val listeners: List<TestEngineListener>) : TestEngineListener {

   constructor(vararg listeners: TestEngineListener) : this(listeners.toList())

   init {
      require(listeners.isNotEmpty()) { "Cannot create CompositeTestEngineListener with no listeners" }
   }

   override suspend fun engineStarted() {
      listeners.forEach { it.engineStarted() }
   }

   override suspend fun engineInitialized(context: EngineContext) {
      listeners.forEach { it.engineInitialized(context) }
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      listeners.forEach { it.engineFinished(t) }
   }

   override suspend fun testStarted(testCase: TestCase) {
      listeners.forEach { it.testStarted(testCase) }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      listeners.forEach { it.testFinished(testCase, result) }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      listeners.forEach { it.testIgnored(testCase, reason) }
   }

   override suspend fun specStarted(ref: SpecRef) {
      listeners.forEach { it.specStarted(ref) }
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      listeners.forEach { it.specFinished(ref, result) }
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      listeners.forEach { it.specIgnored(kclass, reason) }
   }
}
