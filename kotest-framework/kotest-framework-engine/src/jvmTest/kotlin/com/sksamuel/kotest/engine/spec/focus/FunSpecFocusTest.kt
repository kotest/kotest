package com.sksamuel.kotest.engine.spec.focus

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class ExpectFailure : RuntimeException()

class FunSpecFocusTest : FunSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 3
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
      context("other test") {
         test("should never run") {
            error("Boom")
         }
      }
   }
}
