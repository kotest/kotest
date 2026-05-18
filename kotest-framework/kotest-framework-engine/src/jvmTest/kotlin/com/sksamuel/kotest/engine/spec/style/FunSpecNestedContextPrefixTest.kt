package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll

/**
 * Regression test: nested `context(...)` inside a FunSpec container scope was registered
 * without the "context " prefix, while the root-level `context(...)` correctly applied it.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class FunSpecNestedContextPrefixTest : FunSpec() {

   init {
      val capturedPrefixes = mutableListOf<String?>()

      afterAny { (tc, _) ->
         capturedPrefixes.add(tc.name.prefix)
      }

      afterSpec {
         // both the root context and the nested one should have "context " prefix
         capturedPrefixes.shouldContainAll("context ", "context ")
      }

      context("outer") {
         context("inner") {
            test("leaf") { }
         }
      }
   }
}
