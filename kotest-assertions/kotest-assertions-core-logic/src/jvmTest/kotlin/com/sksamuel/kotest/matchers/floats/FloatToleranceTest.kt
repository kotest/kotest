package com.sksamuel.kotest.matchers.floats

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FloatToleranceTest : FunSpec({

   test("float with tolerance should match values within the tolerance") {
      1.0f shouldBe (1.4f plusOrMinus 0.5f)
      1.0f shouldNotBe (1.5f plusOrMinus 0.4f)
   }

   test("Refuse negative tolerance") {
      shouldThrow<IllegalArgumentException> {
         1.0f plusOrMinus -0.1f
      }
   }

   test("Refuse NaN tolerance") {
      shouldThrow<IllegalArgumentException> {
         1.0f plusOrMinus Float.NaN
      }
   }

   test("infinite float with finite tolerance should equal the same infinite float") {
      Float.NEGATIVE_INFINITY shouldBe (Float.NEGATIVE_INFINITY plusOrMinus 1f)
      Float.POSITIVE_INFINITY shouldBe (Float.POSITIVE_INFINITY plusOrMinus 1f)
   }

   test("infinite float should not equal the opposite infinity") {
      Float.POSITIVE_INFINITY shouldNotBe (Float.NEGATIVE_INFINITY plusOrMinus 1f)
   }
})
