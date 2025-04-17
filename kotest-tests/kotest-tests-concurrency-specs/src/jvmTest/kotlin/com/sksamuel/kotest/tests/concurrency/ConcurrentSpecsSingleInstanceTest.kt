package com.sksamuel.kotest.tests.concurrency

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.provided.start
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxOnlyGithubCondition::class)
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
      beforeSpec {
         // we need to reference the start variable so its initialized
         println(start.elapsedNow().inWholeMilliseconds)
      }
      afterProject {
         // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 2000
         // with concurrency it should be ~500
         val duration = start.elapsedNow()
         duration shouldBeLessThan 2.seconds
      }
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxOnlyGithubCondition::class)
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
      afterProject {
         // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 2000
         // with concurrency it should be ~500
         val duration = start.elapsedNow()
         duration shouldBeLessThan 2.seconds
      }
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxOnlyGithubCondition::class)
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
      afterProject {
         // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 2000
         // with concurrency it should be ~500
         val duration = start.elapsedNow()
         duration shouldBeLessThan 2.seconds
      }
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}

// asserts that specs can be executed concurrently safely
@EnabledIf(LinuxOnlyGithubCondition::class)
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
      afterProject {
         // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 2000
         // with concurrency it should be ~500
         val duration = start.elapsedNow()
         duration shouldBeLessThan 2.seconds
      }
      test("a") {
         delay(500)
      }
      test("b") {
      }
   }
}


