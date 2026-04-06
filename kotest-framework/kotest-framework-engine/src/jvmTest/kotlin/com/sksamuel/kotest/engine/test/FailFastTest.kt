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
         results["a"]?.isSuccess shouldBe true
         results["b"]?.isError shouldBe true
         results["c"]?.isIgnored shouldBe true
         results["d"]?.isIgnored shouldBe true
         results.shouldNotContainKey("e")
         // sibling context has its own independent failfast scope
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
         // sibling context has its own independent failfast scope
         results["t"]?.isSuccess shouldBe true
         results["u"]?.isError shouldBe true
         results["v"]?.isIgnored shouldBe true
         results["w"]?.isIgnored shouldBe true
         results.shouldNotContainKey("x")
      }

      test("sibling contexts with failfast are independent (regression for #4944)") {

         val listener = CollectingTestEngineListener()

         TestEngineLauncher().withListener(listener)
            .withSpecRefs(SpecRef.Reference(SiblingContextFailFastFunSpec::class))
            .execute()

         val results = listener.tests.mapKeys { it.key.name.name }
         // context A: a1 fails, a2 is ignored within A's own failfast scope
         results["a1"]?.isError shouldBe true
         results["a2"]?.isIgnored shouldBe true
         // context B: independent scope — b1 runs, b2 fails, b3 ignored
         results["b1"]?.isSuccess shouldBe true
         results["b2"]?.isError shouldBe true
         results["b3"]?.isIgnored shouldBe true
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
   }
}

private class FailFastFunSpec() : FunSpec() {
   init {
      context("context with fail fast enabled").config(failfast = true) {
         test("a") {} // pass
         test("b") { error("boom") }
         test("c") {} // skipped — b failed in this failfast scope
         context("d") {  // skipped — b failed in this failfast scope
            test("e") {} // skipped
         }
      }
      context("context") { // no failfast — always runs
         context("nested context with fail fast enabled").config(failfast = true) {
            test("t") {} // runs — independent failfast scope from outer context
            test("u") { error("boom") } // fails
            test("v") {} // skipped — u failed in this failfast scope
            context("w") {  // skipped — u failed in this failfast scope
               test("x") {} // skipped
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
         "c" {} // skipped — b failed in this failfast scope
         "d" - {  // skipped — b failed in this failfast scope
            "e" {} // skipped
         }
      }
      "context" - { // no failfast — always runs
         "nested context with fail fast enabled".config(failfast = true) - {
            "t" {} // runs — independent failfast scope from outer context
            "u" { error("boom") } // fails
            "v" {} // skipped — u failed in this failfast scope
            "w" - {  // skipped — u failed in this failfast scope
               "x" {} // skipped
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

// Regression spec for https://github.com/kotest/kotest/issues/4944:
// Two sibling contexts each with failfast=true must have independent scopes.
private class SiblingContextFailFastFunSpec : FunSpec() {
   init {
      context("context A").config(failfast = true) {
         test("a1") { error("boom") } // fails
         test("a2") {} // ignored — a1 failed in A's own failfast scope
      }
      context("context B").config(failfast = true) {
         test("b1") {} // passes — B's scope is independent of A's
         test("b2") { error("boom") } // fails
         test("b3") {} // ignored — b2 failed in B's own failfast scope
      }
   }
}
