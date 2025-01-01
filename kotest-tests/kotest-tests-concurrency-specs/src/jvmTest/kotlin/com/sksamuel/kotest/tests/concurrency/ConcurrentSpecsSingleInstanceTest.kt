package com.sksamuel.kotest.tests.concurrency

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxCondition::class)
class ConcurrentSpecsSingleInstanceTest1 : FunSpec() {

   private var befores = ""
   private var afters = ""

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun testCaseOrder() = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      befores += testCase.name.name
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.name.name
   }

   override suspend fun afterSpec(spec: Spec) {
      befores shouldBe "ab"
      afters shouldBe "ab"
   }

   init {
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxCondition::class)
class ConcurrentSpecsSingleInstanceTest2 : FunSpec() {

   private var befores = ""
   private var afters = ""

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun testCaseOrder() = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      befores += testCase.name.name
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.name.name
   }

   override suspend fun afterSpec(spec: Spec) {
      befores shouldBe "ab"
      afters shouldBe "ab"
   }

   init {
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxCondition::class)
class ConcurrentSpecsSingleInstanceTest3 : FunSpec() {

   private var befores = ""
   private var afters = ""

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun testCaseOrder() = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      befores += testCase.name.name
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.name.name
   }

   override suspend fun afterSpec(spec: Spec) {
      befores shouldBe "ab"
      afters shouldBe "ab"
   }

   init {
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxCondition::class)
class ConcurrentSpecsSingleInstanceTest4 : FunSpec() {

   private var befores = ""
   private var afters = ""

   override fun isolationMode() = IsolationMode.SingleInstance
   override fun testCaseOrder() = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      befores += testCase.name.name
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afters += testCase.name.name
   }

   override suspend fun afterSpec(spec: Spec) {
      befores shouldBe "ab"
      afters shouldBe "ab"
   }

   init {
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}
