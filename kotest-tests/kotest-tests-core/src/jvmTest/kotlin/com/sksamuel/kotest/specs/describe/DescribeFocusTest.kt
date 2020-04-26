package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class DescribeFocusTest : DescribeSpec({

   val counter = AtomicInteger(0)

   afterSpec {
      counter.get() shouldBe 2
   }

   describe("f:Foo") {
      it("foo 1") {
         counter.incrementAndGet()
      }
      it("foo 2") {
         counter.incrementAndGet()
      }
   }

   describe("Bar") {
      it("bar") {
         error("boom")
      }
   }

   describe("Baz") {
      it("baz") {
         error("boom")
      }
   }
})
