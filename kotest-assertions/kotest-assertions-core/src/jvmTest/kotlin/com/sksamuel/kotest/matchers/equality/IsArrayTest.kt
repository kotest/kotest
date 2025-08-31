package com.sksamuel.kotest.matchers.equality

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.isArray
import io.kotest.matchers.shouldBe

class IsArrayTest: StringSpec() {
   init {
      "isArray true" {
         isArray(intArrayOf(1, 2, 3)) shouldBe true
         isArray(arrayOf(1, 2, 3)) shouldBe true
         isArray(BooleanArray(1)) shouldBe true
         isArray(CharArray(1)) shouldBe true
         isArray(ByteArray(1)) shouldBe true
         isArray(IntArray(1)) shouldBe true
         isArray(LongArray(1)) shouldBe true
         isArray(FloatArray(1)) shouldBe true
         isArray(DoubleArray(1)) shouldBe true
      }
      "isArray false" {
         isArray(listOf(1, 2, 3)) shouldBe false
         isArray("hello") shouldBe false
         isArray(123) shouldBe false
         isArray(null) shouldBe false
      }
   }
}
