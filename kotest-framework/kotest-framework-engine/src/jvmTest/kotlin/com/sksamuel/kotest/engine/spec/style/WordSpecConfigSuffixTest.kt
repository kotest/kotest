package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for WordSpec config-form `should` producing the wrong test-name
 * suffix (" when" instead of " should").
 *
 * Verifies the suffix on the [TestCase.name] for the container created by
 * `"name".config(...) should { ... }` at the root and inside a `when` container.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class WordSpecConfigSuffixTest : WordSpec() {

   init {
      val capturedSuffixes = mutableMapOf<String, String?>()

      afterAny { (tc, _) ->
         capturedSuffixes[tc.name.name] = tc.name.suffix
      }

      afterSpec {
         capturedSuffixes["with config at root"] shouldBe " should"
         capturedSuffixes["with config nested"] shouldBe " should"
      }

      "with config at root".config(enabled = true) should {
         "leaf at root" { }
      }

      "outer" `when` {
         "with config nested".config(enabled = true) should {
            "leaf nested" { }
         }
      }
   }
}
