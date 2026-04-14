package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class ShouldSpecFocusTest : ShouldSpec() {
   init {
      val counter = AtomicInteger(0)
      afterSpec {
         counter.get() shouldBe 4
      }
      fcontext("top level context") {
         counter.incrementAndGet()
      }
      fcontext("top level context with config").config(enabled = true) {
         counter.incrementAndGet()
      }
      context("not focused container") {
         error("boom")
      }
      fshould("top level test") {
         counter.incrementAndGet()
      }
      fshould("top level test with config").config(enabled = true) {
         counter.incrementAndGet()
      }
      should("should be disabled by focus") {
         error("boom")
      }
   }
}
