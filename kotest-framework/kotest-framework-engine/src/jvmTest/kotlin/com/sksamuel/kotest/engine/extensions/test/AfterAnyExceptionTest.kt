package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
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
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

private class BehaviorSpecWithAfterTestError : BehaviorSpec({
   isolationMode = IsolationMode.InstancePerRoot
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
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   test("fun spec") {}
})

private class StringSpecWithAfterTestError : StringSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   "string test" {}
})

private class ShouldSpecWithAfterTestError : ShouldSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   should("foo") {}
})

private class DescribeSpecWithAfterTestError : DescribeSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
})

private class FeatureSpecWithAfterTestError : FeatureSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   feature("feature") {
      scenario("scenario") { }
   }
})

private class ExpectSpecWithAfterTestError : ExpectSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
})

private class FreeSpecWithAfterTestError : FreeSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   "test" {}
})

private class WordSpecWithAfterTestError : WordSpec({
   isolationMode = IsolationMode.InstancePerRoot
   afterTest {
      error("boom")
   }
   "this test" should {
      "be alive" {}
   }
})

@EnabledIf(LinuxCondition::class)
class AfterAnyExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : AbstractTestEngineListener() {
      override suspend fun testFinished(testCase: TestCase, result: TestResult) {
         if (result is TestResult.Error)
            error = result.cause
      }
   }

   "an exception in before test" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(BehaviorSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(FeatureSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(WordSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(ShouldSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(StringSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(DescribeSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(FreeSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(FunSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM).withListener(listener)
         )
         executor.execute(ExpectSpecWithAfterTestError::class)
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
   }
})
