package com.sksamuel.kotest.engine.extensions.test

import io.kotest.common.Platform
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
   "string test" {}
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

class BeforeAnyExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : AbstractTestEngineListener() {
      override suspend fun testFinished(testCase: TestCase, result: TestResult) {
         if (result.isError)
            error = result.errorOrNull
      }
   }

   "an exception in before test" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(
               listener
            )
         )
         executor.execute(BehaviorSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(FeatureSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(WordSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(ShouldSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(StringSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(DescribeSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(FreeSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(FunSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration().also { it.includePrivateClasses = true }, Platform.JVM).withListener(listener)
         )
         executor.execute(ExpectSpecWithBeforeTestError::class)
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
         error!!.cause!!.shouldHaveMessage("boom")
      }
   }
})
