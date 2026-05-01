package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll

/**
 * Regression test: nested `context(...)` inside a ShouldSpec container scope was registered
 * without the "context " prefix, while root-level `context(...)` (and the test-lambda /
 * config-form variants in [ShouldSpecRootScope]) all applied it.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldSpecNestedContextPrefixTest : ShouldSpec() {

   init {
      val capturedPrefixes = mutableListOf<String?>()

      afterAny { (tc, _) ->
         capturedPrefixes.add(tc.name.prefix)
      }

      afterSpec {
         // both the outer (root) and inner (container) context should have "context " prefix
         capturedPrefixes.shouldContainAll("context ", "context ", "should ")
      }

      context("outer") {
         context("inner") {
            should("test") { }
         }
      }
   }
}
