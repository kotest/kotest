package com.sksamuel.kotest.specs.isolation.test

import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.*
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

private class BehaviorSpecWithBeforeTestError : BehaviorSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   given("given") {
      When("when") {
         then("then") {
         }
      }
   }
})

private class FunSpecWithBeforeTestError : FunSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   test("fun spec") {}
})

private class StringSpecWithBeforeTestError : StringSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   "string test"{}
})

private class ShouldSpecWithBeforeTestError : ShouldSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   should("foo") {}
})

private class DescribeSpecWithBeforeTestError : DescribeSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
})

private class FeatureSpecWithBeforeTestError : FeatureSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   feature("feature") {
      scenario("scenario") { }
   }
})

private class ExpectSpecWithBeforeTestError : ExpectSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
})

private class FreeSpecWithBeforeTestError : FreeSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   "test" {}
})

private class WordSpecWithBeforeTestError : WordSpec({
   isolationMode = IsolationMode.InstancePerTest
   beforeTest {
      error("boom")
   }
   "this test" should {
      "be alive" {}
   }
})

class BeforeTestExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : TestEngineListener {
      override fun testFinished(testCase: TestCase, result: TestResult) {
         if (result.status == TestStatus.Error)
            error = result.error
      }
   }

   "an exception in before test" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(listener)
         executor.execute(BehaviorSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FeatureSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(listener)
         executor.execute(WordSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(listener)
         executor.execute(ShouldSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(listener)
         executor.execute(StringSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(listener)
         executor.execute(DescribeSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FreeSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FunSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(listener)
         executor.execute(ExpectSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
   }
})
