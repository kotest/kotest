package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class FailFastTest : FunSpec() {
   init {

      test("support fail fast on fun spec") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher().withListener(listener)
            .withSpecRefs(SpecRef.Reference((FailFastFunSpec::class)))
            .execute()

         val results = listener.tests.mapKeys { it.key.name.name }

         // first context: failfast stops after "b" fails
         results["a"]?.isSuccess shouldBe true
         results["b"]?.isError shouldBe true
         results["c"]?.isIgnored shouldBe true
         results["d"]?.isIgnored shouldBe true
         results.shouldNotContainKey("e")

         // second context ("context") has no failfast and is unaffected by the first context's failure
         // its nested failfast context ("nested") runs fresh with no prior failures in its scope
         results["t"]?.isSuccess shouldBe true
         results["u"]?.isError shouldBe true
         results["v"]?.isIgnored shouldBe true
         results["w"]?.isIgnored shouldBe true
         results.shouldNotContainKey("x")
      }

      test("support fail fast on free spec") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher().withListener(listener)
            .withSpecRefs(SpecRef.Reference((FailFastFreeSpec::class)))
            .execute()

         val results = listener.tests.mapKeys { it.key.name.name }

         results["a"]?.isSuccess shouldBe true
         results["b"]?.isError shouldBe true
         results["c"]?.isIgnored shouldBe true
         results["d"]?.isIgnored shouldBe true
         results.shouldNotContainKey("e")

         results["t"]?.isSuccess shouldBe true
         results["u"]?.isError shouldBe true
         results["v"]?.isIgnored shouldBe true
         results["w"]?.isIgnored shouldBe true
         results.shouldNotContainKey("x")
      }

      test("fail fast should propagate to all levels") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher().withListener(listener)
            .withSpecRefs(SpecRef.Reference(GrandfatherFailFastFreeSpec::class))
            .execute()

         val results = listener.tests.mapKeys { it.key.name.name }
         results["a"]?.isSuccess shouldBe true
         results["b"]?.isSuccess shouldBe true
         results["c"]?.isSuccess shouldBe true
         results["d"]?.isSuccess shouldBe true
         results["e"]?.isError shouldBe true
         results["f"]?.isIgnored shouldBe true
         results["g"]?.isIgnored shouldBe true
         results["h"].shouldBeNull()
      }

      // Regression test for https://github.com/kotest/kotest/issues/4944:
      // A failfast failure inside one context must not cause sibling contexts to be skipped.
      test("fail fast in one context should not skip sibling contexts") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher().withListener(listener)
            .withSpecRefs(SpecRef.Reference(FailFastSiblingContextSpec::class))
            .execute()

         val results = listener.tests.mapKeys { it.key.name.name }

         // context1: failfast stops after "b" fails
         results["a"]?.isSuccess shouldBe true
         results["b"]?.isError shouldBe true
         results["c"]?.isIgnored shouldBe true

         // context2: entirely independent — its failfast scope has had no failures
         results["d"]?.isSuccess shouldBe true
         results["e"]?.isSuccess shouldBe true
      }
   }
}

private class FailFastFunSpec() : FunSpec() {
   init {
      context("context with fail fast enabled").config(failfast = true) {
         test("a") {} // pass
         test("b") { error("boom") }
         test("c") {} // skipped: failfast triggered in this context
         context("d") {  // skipped: failfast triggered in parent context
            test("e") {} // not reached: parent skipped
         }
      }
      context("context") { // no failfast — runs regardless of the first context's failure
         context("nested context with fail fast enabled").config(failfast = true) {
            test("t") {} // pass: this is a fresh failfast scope with no prior failure
            test("u") { error("boom") }
            test("v") {} // skipped: failfast triggered in this context
            context("w") {  // skipped: failfast triggered in parent context
               test("x") {} // not reached: parent skipped
            }
         }
      }
   }
}

private class FailFastFreeSpec() : FreeSpec() {
   init {
      "context with fail fast enabled".config(failfast = true) - {
         "a" {} // pass
         "b" { error("boom") }
         "c" {} // skipped
         "d" - {  // skipped
            "e" {} // not reached
         }
      }
      "context" - { // no failfast — runs regardless of the first context's failure
         "nested context with fail fast enabled".config(failfast = true) - {
            "t" {} // pass: fresh failfast scope
            "u" { error("boom") }
            "v" {} // skipped
            "w" - {  // skipped
               "x" {} // not reached
            }
         }
      }
   }
}

private class GrandfatherFailFastFreeSpec() : FreeSpec() {
   init {
      "a".config(failfast = true) - {
         "b" {} // pass
         "c" - {
            "d" {} // pass
            "e" { error("boom") }
            "f" {} // will be skipped
         }
         "g" - {  // should fail because c has failed
            "h" {}
         }
      }
   }
}

// Reproduces the sibling-context regression from https://github.com/kotest/kotest/issues/4944
private class FailFastSiblingContextSpec : FunSpec() {
   init {
      context("context1").config(failfast = true) {
         test("a") {}
         test("b") { error("boom") }
         test("c") {} // skipped
      }
      context("context2").config(failfast = true) {
         test("d") {} // must NOT be skipped
         test("e") {} // must NOT be skipped
      }
   }
}
