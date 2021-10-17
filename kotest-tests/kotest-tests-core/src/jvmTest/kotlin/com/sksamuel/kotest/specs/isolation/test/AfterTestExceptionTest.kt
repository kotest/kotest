package com.sksamuel.kotest.specs.isolation.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

private class BehaviorSpecWithAfterTestError : BehaviorSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   given("given") {
      When("when") {
         then("then") {
         }
      }
   }
})

private class FunSpecWithAfterTestError : FunSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   test("fun spec") {}
})

private class StringSpecWithAfterTestError : StringSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   "string test"{}
})

private class ShouldSpecWithAfterTestError : ShouldSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   should("foo") {}
})

private class DescribeSpecWithAfterTestError : DescribeSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
})

private class FeatureSpecWithAfterTestError : FeatureSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   feature("feature") {
      scenario("scenario") { }
   }
})

private class ExpectSpecWithAfterTestError : ExpectSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
})

private class FreeSpecWithAfterTestError : FreeSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   "test" {}
})

private class WordSpecWithAfterTestError : WordSpec({
   isolationMode = IsolationMode.InstancePerTest
   afterTest {
      error("boom")
   }
   "this test" should {
      "be alive" {}
   }
})

class AfterTestExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : AbstractTestEngineListener() {
      override suspend fun testFinished(testCase: TestCase, result: TestResult) {
         if (result.status == TestStatus.Error)
            error = result.error
      }
   }

   "an exception in before test" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(BehaviorSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(FeatureSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(WordSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(ShouldSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(StringSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(DescribeSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(FreeSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(FunSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(listener, NoopCoroutineDispatcherFactory)
         executor.execute(ExpectSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<IllegalStateException>()
         error!!.shouldHaveMessage("boom")
      }
   }
})
