package com.sksamuel.kotest.engine.spec.config

import io.kotest.common.nonConstantFalse
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.config.TestConfig
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

/**
 * A test that just ensures the syntax for test configs does not break between releases.
 * The actual functionality of things like tags and timeouts is tested elsewhere.
 */
@EnabledIf(NotMacOnGithubCondition::class)
class FreeSpecConfigSyntaxTest : FreeSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 19
      }

      "a test disabled by an enabled flag".config(enabled = false) {
         error("boom")
      }

      "a test disabled by an enabled function".config(enabledIf = { nonConstantFalse() }) {
         error("boom")
      }

      "a test with multiple invocations".config(invocations = 2) {
         counter.incrementAndGet()
      }

      "a test with timeout".config(timeout = 1.seconds) {
         counter.incrementAndGet()
      }

      "a test with tags".config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
      }

      "a test with multiple tags".config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
      }

      val config = TestConfig(enabled = false)
      "a test with overloaded config".config(config) {
         error("boom")
      }

      "a context with overloaded config" - {
         "disabled from config object".config(config) {
            error("boom")
         }
      }

      "an outer context with timeout".config(timeout = 2.seconds) - {
         counter.incrementAndGet()
         "an inner test" {
            counter.incrementAndGet()
         }
      }

      "an outer context with tags".config(tags = setOf(Tag1)) - {
         counter.incrementAndGet()
         "an inner test" {
            counter.incrementAndGet()
         }
      }

      "an outer context with multiple tags".config(tags = setOf(Tag1, Tag2)) - {
         counter.incrementAndGet()
         "an inner test" {
            counter.incrementAndGet()
         }
      }

      "an outer context disabled by an enabled flag".config(enabled = false) - {
         error("boom")
      }

      "an outer context disabled by an enabled function".config(enabledIf = { nonConstantFalse() }) - {
         error("boom")
      }

      "an outer context" - {

         "inner" {
            counter.incrementAndGet()
         }

         counter.incrementAndGet()
         "an inner context with timeout".config(timeout = 2.seconds) - {
            counter.incrementAndGet()
            "an inner test" {
               counter.incrementAndGet()
            }
         }

         "an inner context with tags".config(tags = setOf(Tag1)) - {
            counter.incrementAndGet()
            "an inner test" {
               counter.incrementAndGet()
            }
         }

         "an inner context with multiple tags".config(tags = setOf(Tag1, Tag2)) - {
            counter.incrementAndGet()
            "an inner test" {
               counter.incrementAndGet()
            }
         }

         "an inner context disabled by an enabled flag".config(enabled = false) - {
            error("boom")
         }

         "an inner context disabled by an enabled function".config(enabledIf = { nonConstantFalse() }) - {
            error("boom")
         }
      }
   }
}
