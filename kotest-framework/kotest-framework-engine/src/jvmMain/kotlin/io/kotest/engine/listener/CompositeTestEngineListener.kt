package io.kotest.engine.listener

import io.kotest.core.plan.TestPlanNode
import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

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

   override fun testFinished(description: Description, result: TestResult) {
      listeners.forEach { it.testFinished(description, result) }
   }

   override fun specFinished(
      spec: TestPlanNode.SpecNode,
      t: Throwable?,
      results: Map<TestPlanNode.TestCaseNode, TestResult>
   ) {
      listeners.forEach { it.specFinished(spec, t, results) }
   }

   override fun specStarted(spec: TestPlanNode.SpecNode) {
      listeners.forEach { it.specStarted(spec) }
   }

   override fun testStarted(description: Description) {
      listeners.forEach { it.testStarted(description) }
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
