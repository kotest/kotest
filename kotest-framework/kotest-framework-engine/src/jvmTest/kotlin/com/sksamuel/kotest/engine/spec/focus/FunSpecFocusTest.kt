package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class FunSpecFocusTest : FunSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 6
      }
      fcontext("top level focused context") {
         counter.incrementAndGet()
      }
      fcontext("top level focused context with config").config(enabled = true) {
         counter.incrementAndGet()
      }
      context("f:some test") {
         counter.incrementAndGet()
         test("should run") {
            counter.incrementAndGet()
         }
         test("should run 2") {
            counter.incrementAndGet()
         }
      }
      context("f:some test with config").config(enabled = true) {
         counter.incrementAndGet()
      }
      context("other test") {
         error("Boom")
      }
   }
}
