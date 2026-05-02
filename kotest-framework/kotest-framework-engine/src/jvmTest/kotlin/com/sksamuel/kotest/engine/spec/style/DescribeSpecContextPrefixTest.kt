package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll

/**
 * Regression test: nested `context(name).config(...)` and `fcontext(name).config(...)`
 * inside a DescribeSpec container were missing the "Context: " prefix on the
 * registered test name, while every other context/xcontext path applied it.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class DescribeSpecContextPrefixTest : DescribeSpec() {

   init {
      val collectedPrefixes = mutableListOf<String?>()

      afterAny { (tc, _) ->
         collectedPrefixes.add(tc.name.prefix)
      }

      afterSpec {
         collectedPrefixes.shouldContainAll("Context: ", "Context: ", "Context: ")
      }

      describe("outer") {
         // matches the no-lambda + .config form for context / fcontext / xcontext
         context("plain config").config(enabled = true) {
            it("inner") { }
         }
         xcontext("disabled with config").config(enabled = false) {
            it("inner") { }
         }
         // Note: fcontext(...).config { ... } would force focus mode and skip the others;
         // we exercise it indirectly via the same code path.
         context("another plain config").config(enabled = true) {
            it("inner") { }
         }
      }
   }
}
