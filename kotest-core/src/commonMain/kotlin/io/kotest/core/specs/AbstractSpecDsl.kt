package io.kotest.core.specs

import io.kotest.AssertionMode
import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.Tag
import io.kotest.TestCase
import io.kotest.TestCaseOrder
import io.kotest.TestResult
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestListener

abstract class AbstractSpecDsl : AbstractSpec() {

   private var beforeTestFn: (TestCase) -> Unit = {}
   private var afterTestFn: (TestCase, TestResult) -> Unit = { _, _ -> }
   private var beforeSpecFn: (Spec) -> Unit = {}
   private var afterSpecFn: (Spec) -> Unit = {}
   private var afterSpecClassFn: (Spec, Map<TestCase, TestResult>) -> Unit = { _, _ -> }
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
      beforeTestFn(testCase)
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      super.afterTest(testCase, result)
      afterTestFn(testCase, result)
   }

   override fun beforeSpec(spec: Spec) {
      super.beforeSpec(spec)
      beforeSpecFn(spec)
   }

   override fun afterSpec(spec: Spec) {
      super.afterSpec(spec)
      afterSpecFn(spec)
   }

   override fun tags(): Set<Tag> {
      return super.tags()
   }

   override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
      super.afterSpecClass(spec, results)
      afterSpecClassFn(spec, results)
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

   fun beforeTest(f: (TestCase) -> Unit) {
      beforeTestFn = f
   }

   fun afterTest(f: (TestCase, TestResult) -> Unit) {
      afterTestFn = f
   }

   fun beforeSpec(f: (Spec) -> Unit) {
      beforeSpecFn = f
   }

   fun afterSpec(f: (Spec) -> Unit) {
      afterSpecFn = f
   }

   fun afterSpecClass(f: (Spec, Map<TestCase, TestResult>) -> Unit) {
      afterSpecClassFn = f
   }
}
