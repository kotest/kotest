package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import org.junit.jupiter.api.Assertions.assertEquals

class DescribeFocusTest : DescribeSpec({
   describe("f:Foo") {
      it("foo") {
         assertEquals(1, 2)
      }
   }

   describe("Bar") {
      it("bar") {
         assertEquals(1, 2)
      }
   }

   describe("Baz") {
      it("baz") {
         assertEquals(1, 2)
      }
   }
})
