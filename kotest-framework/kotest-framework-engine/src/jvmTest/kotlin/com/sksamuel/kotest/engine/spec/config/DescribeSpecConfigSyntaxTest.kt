package com.sksamuel.kotest.engine.spec.config

import io.kotest.common.ExperimentalKotest
import io.kotest.common.nonConstantFalse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.config.TestConfig
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

/**
 * A test that just ensures the syntax for test configs does not break between releases.
 * The actual functionality of things like tags and timeouts is tested elsewhere.
 */
@ExperimentalKotest
class DescribeSpecConfigSyntaxTest : DescribeSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 24
      }

      describe("a describe with timeout").config(timeout = 2.seconds) {
          counter.incrementAndGet()
          it("an inner test") {
              counter.incrementAndGet()
          }
      }

      describe("a describe with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         it("an inner test") {
            counter.incrementAndGet()
         }
      }

      describe("a describe with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         it("an inner test") {}
      }

      describe("a describe with overloaded config") {
         val config = TestConfig(enabled = false)
         it("disabled from config object").config(config) {
            error("boom")
         }
      }

      describe("a describe disabled by an enabled flag").config(enabled = false) {
         error("boom")
         it("an inner test") { error("boom") }
      }

      describe("a describe disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
         error("boom")
         it("an inner test") { error("boom") }
      }

      describe("a describe disabled by an enabled function with reason").config(enabledOrReasonIf = { Enabled.disabled }) {
         error("boom")
         it("an inner test") { error("boom") }
      }

      xdescribe("a xdescribe with config").config(enabled = false) {
         it("an inner test") { error("boom") }
      }

      context("a context") {
         counter.incrementAndGet()
         describe("a describe with timeout").config(timeout = 2.seconds) {
             counter.incrementAndGet()
             it("an inner test") {
                 counter.incrementAndGet()
             }
         }

         describe("a describe with tags").config(tags = setOf(Tag1)) {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }

         describe("a describe with multiple tags").config(tags = setOf(Tag1, Tag2)) {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }

         describe("a describe disabled by an enabled flag").config(enabled = false) {
            error("boom")
            it("an inner test") { error("boom") }
         }

         describe("a describe disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
            error("boom")
            it("an inner test") { error("boom") }
         }

         xdescribe("a nested disabled xdescribe with config").config(enabled = false) {
            it("an inner test") { error("boom") }
         }
      }

      context("a context with timeout").config(timeout = 2.seconds) {
          context("a nested context with timeout").config(timeout = 2.seconds) {
              counter.incrementAndGet()
              describe("a describe") {
                  counter.incrementAndGet()
                  it("an inner test") {
                      counter.incrementAndGet()
                  }
              }
          }
          xcontext("a nested disabled conext").config(enabled = false) {
              it("an inner test") {
                  error("boom")
              }
          }

          counter.incrementAndGet()
          describe("a describe") {
              counter.incrementAndGet()
              it("an inner test") {
                  counter.incrementAndGet()
              }
          }
      }

      context("a context with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context disabled by an enabled flag").config(enabled = false) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context disabled by an enabled function").config(enabledIf = { nonConstantFalse() }) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      xcontext("an xdisabled xcontent with config").config(enabled = false) {
         it("an inner test") { error("boom") }
      }
   }
}
