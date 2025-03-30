package com.sksamuel.kotest.engine.spec.config

import io.kotest.common.nonConstantFalse
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

/**
 * A test that just ensures the syntax for test configs does not break between releases.
 * The actual functionality of things like tags and timeouts is tested elsewhere.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldSpecConfigSyntaxTest : ShouldSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 17
      }

      should("a test disabled by an enabled flag").config(enabled = false) {
         error("boom")
      }

      should("a test disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
         error("boom")
      }

      should("a test with multiple invocations").config(invocations = 2) {
         counter.incrementAndGet()
      }

      should("a test with timeout").config(timeout = 1.seconds) {
          counter.incrementAndGet()
      }

      should("a test with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
      }

      should("a test with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
      }

      context("an outer context with timeout").config(timeout = 2.seconds) {
          counter.incrementAndGet()
          should("an inner test") {
              counter.incrementAndGet()
          }
      }

      context("an outer context with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         should("an inner test") {
            counter.incrementAndGet()
         }
      }

      context("an outer context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         should("an inner test") {}
      }

      context("an outer context disabled by an enabled flag").config(enabled = false) {
         error("boom")
         should("an inner test") { error("boom") }
      }

      context("an outer context disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
         error("boom")
         should("an inner test") { error("boom") }
      }

      context("an outer context") {
         counter.incrementAndGet()
         context("an inner context with timeout").config(timeout = 2.seconds) {
             counter.incrementAndGet()
             should("an inner test") {
                 counter.incrementAndGet()
             }
         }

         context("an inner context with tags").config(tags = setOf(Tag1)) {
            counter.incrementAndGet()
            should("an inner test") {
               counter.incrementAndGet()
            }
         }

         context("an inner context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
            counter.incrementAndGet()
            should("an inner test") {
               counter.incrementAndGet()
            }
         }

         context("an inner context disabled by an enabled flag").config(enabled = false) {
            error("boom")
            should("an inner test") { error("boom") }
         }

         context("an inner context disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
            error("boom")
            should("an inner test") { error("boom") }
         }
      }
   }
}
