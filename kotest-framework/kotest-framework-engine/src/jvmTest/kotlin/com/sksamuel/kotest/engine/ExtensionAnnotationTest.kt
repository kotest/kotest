package com.sksamuel.kotest.engine

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@ApplyExtension(MyTestListener::class)
class ExtensionAnnotationTest : WordSpec({
   "@Extension annotation" should {
      "be detected for all tests" {
         MyTestListener.beforeCounter.get() shouldBe 6
         MyTestListener.afterCounter.get() shouldBe 0
      }
      "even this one!" {
         MyTestListener.beforeCounter.get() shouldBe 9
         MyTestListener.afterCounter.get() shouldBe 3
      }
      MyTestListener.beforeCounter.get() shouldBe 9
      MyTestListener.afterCounter.get() shouldBe 6
   }
})

object MyTestListener : TestListener {

   val beforeCounter = AtomicInteger(0)
   val afterCounter = AtomicInteger(0)

   override suspend fun beforeTest(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeEach(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeAny(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   private fun maybeIncrementBeforeCounter(testCase: TestCase) {
      if (testCase.spec::class.java.name == ExtensionAnnotationTest::class.java.name)
         beforeCounter.incrementAndGet()
   }

   private fun maybeIncrementAfterCounter(testCase: TestCase) {
      if (testCase.spec::class.java.name == ExtensionAnnotationTest::class.java.name)
         afterCounter.incrementAndGet()
   }
}
