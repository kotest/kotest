package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class FailFastTest : FunSpec() {
   init {

      context("support fail fast on specs") {
         withData(
            FailFastFunSpec::class,
            FailFastFreeSpec::class,
            FailFastDesscribeSpec::class,
         ) { spec ->
            val listener = CollectingTestEngineListener()

            TestEngineLauncher(listener)
               .withClasses(spec)
               .launch()

            val results = listener.tests.mapKeys { it.key.name.name }
            results["context1 with fail fast enabled"]?.isSuccess shouldBe true
            results["a"]?.isSuccess shouldBe true
            results["b"]?.isError shouldBe true
            results["c"]?.isIgnored shouldBe true
            results["d"]?.isIgnored shouldBe true
            results["e"] shouldBe null
            results["context2 with fail fast enabled"]?.isSuccess shouldBe true
            results["m"]?.isSuccess shouldBe true
            results["context3"]?.isSuccess shouldBe true
            results["s"]?.isError shouldBe true
            results["t"]?.isSuccess shouldBe true
         }
      }
   }
}

private class FailFastFunSpec() : FunSpec() {
   init {
      context("context1 with fail fast enabled").config(failfast = true) {
         test("a") {} // pass
         test("b") { error("boom") }
         test("c") {} // will be skipped
         context("d") {  // skipped
            test("e") {} // not even discovered because d is skipped
         }
      }
      context("context2 with fail fast enabled").config(failfast = true) { // should run independently of the failed test in context1
         test("m") {}
      }
      context("context3") { // this will run regardless because it has no fail fast setting
         test("s") { error("boom") }
         test("t") {} // will be shown because the parent context does not have fail fast enabled
      }
   }
}

private class FailFastFreeSpec() : FreeSpec() {
   init {
      "context1 with fail fast enabled".config(failfast = true) - {
         "a" {} // pass
         "b" { error("boom") }
         "c" {} // will be skipped
         "d" - {  // skipped
            "e" {} // not even discovered because d is skipped
         }
      }
      "context2 with fail fast enabled".config(failfast = true) - {  // should run independently of the failed test in context1
         "m" {}
      }
      "context3" - { // this will run regardless because it has no fail fast setting
         "s" { error("boom") }
         "t" {} // will be shown because the parent context does not have fail fast enabled
      }
   }
}

private class FailFastDesscribeSpec() : DescribeSpec() {
   init {
      describe("context1 with fail fast enabled").config(failfast = true) {
         it("a") {} // pass
         it("b") { error("boom") }
         it("c") {} // will be skipped
         describe("d") {  // skipped
            it("e") {} // not even discovered because d is skipped
         }
      }
      describe("context2 with fail fast enabled").config(failfast = true) { // should run independently of the failed test in context1
         it("m") {}
      }
      describe("context3") { // this will run regardless because it has no fail fast setting
         it("s") { error("boom") }
         it("t") {} // will be shown because the parent context does not have fail fast enabled
      }
   }
}
