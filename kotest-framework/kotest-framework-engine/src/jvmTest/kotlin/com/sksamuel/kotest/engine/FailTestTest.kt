package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class FailTestTest : FunSpec() {
   init {
      test("when enabling fail fast, further nested tests should be skipped") {
         val listener = CapturingTestListener()
         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(FailTestFunSpec::class)
            .launch()

         val results = listener.testsFinished.mapKeys { it.key.name }
         results["a"] shouldBe TestStatus.Success
         results["b"] shouldBe TestStatus.Error
         results["c"] shouldBe TestStatus.Ignored
         results["d"] shouldBe TestStatus.Ignored
         results.shouldNotContainKey("e")
         results["t"] shouldBe TestStatus.Success
         results["u"] shouldBe TestStatus.Error
         results["v"] shouldBe TestStatus.Ignored
         results["w"] shouldBe TestStatus.Ignored
         results.shouldNotContainKey("x")
      }
   }
}

class CapturingTestListener : TestEngineListener {

   val specsFinished = ConcurrentHashMap<KClass<*>, Throwable?>()
   val testsFinished = ConcurrentHashMap<DescriptionName.TestName, TestStatus>()

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      specsFinished[kclass] = t
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      testsFinished[testCase.description.name] = TestStatus.Ignored
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      testsFinished[testCase.description.name] = result.status
   }
}


private class FailTestFunSpec() : FunSpec() {
   init {
      context("root context with fail fast enabled").config(failfast = true) {
         test("a") {} // pass
         test("b") { error("boom") }
         test("c") {} // will be skipped
         context("d") {  // skipped
            test("e") {} // skipped
         }
      }
      context("root") {
         context("nested context with fail fast enabled").config(failfast = true) {
            test("t") {} // pass
            test("u") { error("boom") }
            test("v") {} // will be skipped
            context("w") {  // skipped
               test("x") {} // skipped
            }
         }
      }
   }
}
