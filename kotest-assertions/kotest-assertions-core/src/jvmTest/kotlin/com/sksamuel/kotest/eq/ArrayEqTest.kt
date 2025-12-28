package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.ArrayEq
import io.kotest.assertions.eq.EqContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith

class ArrayEqTest : FunSpec({

   test("should give null for two equal arrays") {
      ArrayEq.equals(arrayOf(1, 2, 3), arrayOf(1, 2, 3), false, EqContext()).shouldBeNull()
   }

   test("should give error for two unequal arrays") {
      val error = ArrayEq.equals(arrayOf(3), arrayOf(1, 2, 3), false, EqContext())

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [0]
                                  |Missing elements from index 1
                                  |expected:<[1, 2, 3]> but was:<[3]>""".trimMargin()
      }
   }

   test("should work for empty arrays") {
      val errorMessage1 = ArrayEq.equals(emptyArray<Int>(), arrayOf(1), false, EqContext())?.message
      errorMessage1 shouldBe """Missing elements from index 0
                               |expected:<[1]> but was:<[]>""".trimMargin()

      val errorMessage2 = ArrayEq.equals(arrayOf(1, 2), emptyArray<Int>(), false, EqContext())?.message
      errorMessage2 shouldBe """Unexpected elements from index 1
                               |expected:<[]> but was:<[1, 2]>""".trimMargin()
   }

   test("should disallow nested arrays") {
      val array1 = arrayOf(arrayOf(1, 2), 3)
      val array2 = arrayOf(arrayOf(1, 2), 3)

      val error = ArrayEq.equals(array1, array2, false, EqContext())
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldStartWith "Disallowed nesting array"
         error.message shouldContain "(Array) within"
         error.message shouldContain "(use custom test code instead)"
      }
   }
})
