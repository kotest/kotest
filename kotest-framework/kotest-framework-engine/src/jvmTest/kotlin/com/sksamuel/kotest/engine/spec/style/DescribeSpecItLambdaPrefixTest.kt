package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test: nested `it(name) { ... }` / `fit(name) { ... }` / `xit(name) { ... }` inside a
 * DescribeSpec container were registered without the "It: " prefix, while the no-lambda config
 * variants (`it("name").config(...) { }`) on the same scope did apply it. The lambda variants
 * also wrapped the test body in a `DescribeSpecContainerScope`, leaking container DSL methods
 * (describe/context/it) into what should be a leaf test scope.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class DescribeSpecItLambdaPrefixTest : DescribeSpec() {

   init {
      val capturedPrefixes = mutableMapOf<String, String?>()

      afterAny { (tc, _) ->
         capturedPrefixes[tc.name.name] = tc.name.prefix
      }

      afterSpec {
         capturedPrefixes["leaf via lambda"] shouldBe "It: "
      }

      describe("outer") {
         it("leaf via lambda") { }
      }
   }
}
