package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestStatus
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe

class FailFastTest : FunSpec() {
   init {
      test("when enabling fail fast, further nested tests should be skipped") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher(listener)
            .withClasses(FailFastFunSpec::class)
            .async()

         val results = listener.tests.mapKeys { it.key.displayName }
         results["a"]?.status shouldBe TestStatus.Success
         results["b"]?.status shouldBe TestStatus.Error
         results["c"]?.status shouldBe TestStatus.Ignored
         results["d"]?.status shouldBe TestStatus.Ignored
         results.shouldNotContainKey("e")
         results["t"]?.status shouldBe TestStatus.Success
         results["u"]?.status shouldBe TestStatus.Error
         results["v"]?.status shouldBe TestStatus.Ignored
         results["w"]?.status shouldBe TestStatus.Ignored
         results.shouldNotContainKey("x")
      }
   }
}

private class FailFastFunSpec() : FunSpec() {
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
