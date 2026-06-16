package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.ArrayEq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf

class ArrayEqTest : FunSpec({

   test("should give null for two equal arrays") {
      ArrayEq.equals(arrayOf(1, 2, 3), arrayOf(1, 2, 3), EqContext()).shouldBeInstanceOf<EqResult.Success>()
   }

   test("should give error for two unequal arrays") {
      val result = ArrayEq.equals(arrayOf(3), arrayOf(1, 2, 3), EqContext()) as EqResult.Failure
      val error = result.error()

      assertSoftly {
         error.message shouldBe """Element differ at index: [0]
                                  |Missing elements from index 1
                                  |expected:<[1, 2, 3]> but was:<[3]>""".trimMargin()
      }
   }

   test("should work for empty arrays") {
      val result1 = ArrayEq.equals(emptyArray<Int>(), arrayOf(1), EqContext()) as EqResult.Failure
      val error1 = result1.error()
      error1.message shouldBe """Missing elements from index 0
                               |expected:<[1]> but was:<[]>""".trimMargin()

      val result2 = ArrayEq.equals(arrayOf(1, 2), emptyArray<Int>(), EqContext()) as EqResult.Failure
      val error2 = result2.error()
      error2.message shouldBe """Unexpected elements from index 1
                               |expected:<[]> but was:<[1, 2]>""".trimMargin()
   }

   test("should disallow nested arrays") {
      val array1 = arrayOf(arrayOf(1, 2), 3)
      val array2 = arrayOf(arrayOf(1, 2), 3)

      val result = ArrayEq.equals(array1, array2, EqContext()) as EqResult.Failure
      val error = result.error()
      assertSoftly {
         error.message shouldStartWith "Disallowed nesting array"
         error.message shouldContain "(Array) within"
         error.message shouldContain "(use custom test code instead)"
      }
   }
})
