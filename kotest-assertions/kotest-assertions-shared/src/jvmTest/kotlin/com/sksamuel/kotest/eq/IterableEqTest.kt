package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.IterableEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class IterableEqTest : FunSpec({
   test("should give null for two equal sets") {
      IterableEq.equals(setOf(1, 2, 3), setOf(2, 3, 1)).shouldBeNull()
   }

   test("should give error for unequal tests") {
      val error = IterableEq.equals(setOf(1, 2, 3), setOf(2, 3))

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe "expected:<[2, 3]> but was:<[1, 2, 3]>"
      }
   }

   test("should give null for two equal list") {
      IterableEq.equals(listOf(1, 2, 3), listOf(1, 2, 3)).shouldBeNull()
   }

   test("should give error for two unequal list") {
      val error = IterableEq.equals(listOf(3), listOf(1, 2, 3))

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe "expected:<[1, 2, 3]> but was:<[3]>"
      }
   }

   test("should give null for equal deeply nested arrays") {
      val array1 = arrayListOf(1, arrayOf(arrayListOf("a", "c"), "b"), mapOf("a" to arrayListOf(1 ,2, 3)))
      val array2 = arrayListOf(1, arrayOf(arrayListOf("a", "c"), "b"), mapOf("a" to arrayListOf(1 ,2, 3)))

      IterableEq.equals(array1.toList(), array2.toList()).shouldBeNull()
   }

   test("should give error for unequal deeply nested arrays") {
      val array1 = arrayListOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayListOf(1 ,2, 3)))
      val array2 = arrayListOf(1, arrayOf(arrayOf("a", "e"), "b"), mapOf("a" to arrayListOf(1 ,2, 3)))

      val error = IterableEq.equals(array1.toList(), array2.toList())

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[1, [["a", "e"], "b"], [("a", [1, 2, 3])]]> but was:<[1, [["a", "c"], "b"], [("a", [1, 2, 3])]]>"""
      }
   }
})
