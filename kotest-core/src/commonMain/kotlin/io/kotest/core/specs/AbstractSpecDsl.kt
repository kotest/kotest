package io.kotest.core.specs

import io.kotest.SpecClass
import io.kotest.core.*
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestListener

abstract class AbstractSpecDsl : AbstractSpec() {

   private var afterSpecClassFn: (SpecClass, Map<TestCase, TestResult>) -> Unit = { _, _ -> }
   private var tags: Set<Tag> = emptySet()
   private var listeners: List<TestListener> = emptyList()
   private var extensions: List<SpecLevelExtension> = emptyList()
   private var testCaseOrder: TestCaseOrder? = null
   private var isolationMode: IsolationMode? = null
   private var assertionMode: AssertionMode? = null

   override fun testCaseOrder(): TestCaseOrder? = testCaseOrder
   override fun isolationMode(): IsolationMode? = isolationMode
   override fun assertionMode(): AssertionMode? = assertionMode

   override fun listeners(): List<TestListener> = listeners
   override fun extensions(): List<SpecLevelExtension> = extensions

   override fun beforeTest(testCase: TestCase) {
      super.beforeTest(testCase)
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      super.afterTest(testCase, result)
   }

   override fun beforeSpec(spec: SpecConfiguration) {
      super.beforeSpec(spec)
   }

   override fun afterSpec(spec: SpecConfiguration) {
      super.afterSpec(spec)
   }

   fun beforeSpec(spec: SpecClass) {
   }

   fun afterSpec(spec: SpecClass) {
   }

   override fun tags(): Set<Tag> {
      return super.tags()
   }

   override fun afterSpecClass(spec: SpecConfiguration, results: Map<TestCase, TestResult>) {
      super.afterSpecClass(spec, results)
   }

   fun set(isolationMode: IsolationMode) {
      this.isolationMode = isolationMode
   }

   fun set(assertionMode: AssertionMode) {
      this.assertionMode = assertionMode
   }

   fun set(testCaseOrder: TestCaseOrder) {
      this.testCaseOrder = testCaseOrder
   }

   fun tags(vararg tags: Tag) {
      this.tags = tags.toSet()
   }

   fun listeners(vararg listeners: TestListener) {
      this.listeners = listeners.toList()
   }

   fun extensions(vararg extensions: SpecLevelExtension) {
      this.extensions = extensions.toList()
   }

   fun afterSpecClass(f: (SpecClass, Map<TestCase, TestResult>) -> Unit) {
      afterSpecClassFn = f
   }
}
