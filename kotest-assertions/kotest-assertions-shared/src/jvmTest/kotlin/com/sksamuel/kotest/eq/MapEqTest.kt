package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.MapEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class MapEqTest : FunSpec({
   test("should give null for simple equal maps") {
      val equals = MapEq.equals(emptyMap<Any, Any>(), emptyMap<Any, Any>())

      equals.shouldBeNull()
   }

   test("should give error for simple not equal maps") {
      val map1 = mapOf("a" to "b")
      val map2 = mapOf("a" to "c")

      val throwable = MapEq.equals(map1, map2)

      assertSoftly {
         throwable.shouldNotBeNull()
         throwable.message shouldBe """
            Expected
            {
              "a" = "c"
            }
            to be equal to
            {
              "a" = "b"
            }
            Values differed at keys a
         """.trimIndent()
      }
   }

   test("should give null for complex equal maps") {
      val map1 = mapOf("a" to arrayOf(1,2))
      val map2 = mapOf("a" to arrayOf(1,2))

      MapEq.equals(map1, map2).shouldBeNull()
   }

   test("should give null for deeply nested equal maps") {
      val actual = mapOf(
         "1" to mapOf(
            "2" to mapOf(
               "3" to mapOf(
                  "4" to "value"
               )
            )
         )
      )
      val expected = mapOf(
         "1" to mapOf(
            "2" to mapOf(
               "3" to mapOf(
                  "4" to "value"
               )
            )
         )
      )

      MapEq.equals(actual, expected).shouldBeNull()
   }

   test("should give error for deeply nested not equal maps") {
      val actual = mapOf(
         "1" to mapOf(
            "2" to mapOf(
               "3" to mapOf(
                  "4" to arrayOf("foo")
               )
            )
         )
      )
      val expected = mapOf(
         "1" to mapOf(
            "2" to mapOf(
               "3" to "bar"
            )
         )
      )

      val throwable = MapEq.equals(actual, expected)
      assertSoftly {
         throwable.shouldNotBeNull()
         throwable.message shouldBe """
            Expected
            {
              "1" = [("2", [("3", "bar")])]
            }
            to be equal to
            {
              "1" = [("2", [("3", [("4", ["foo"])])])]
            }
            Values differed at keys 1
         """.trimIndent()
      }
   }

   test("should give null for equal maps having map as keys") {

      val map1 = mapOf(
         mapOf("a" to "b") to mapOf(
            "a" to arrayOf(1, 2, 3)
         )
      )
      val map2 = mapOf(
         mapOf("a" to "b") to mapOf(
            "a" to arrayOf(1, 2, 3)
         )
      )
      MapEq.equals(map1, map2).shouldBeNull()
   }

   test("should give error for non equal maps having map as keys") {

      val map1 = mapOf(
         mapOf("a" to "b") to mapOf(
            "a" to arrayOf(1, 2, 3)
         )
      )
      val map2 = mapOf(
         mapOf("a" to "c") to mapOf(
            "a" to arrayOf(1, 2, 3)
         )
      )
      val throwable = MapEq.equals(map1, map2)
      assertSoftly {
         throwable.shouldNotBeNull()
         throwable.message shouldBe """
            Expected
            {
              [("a", "c")] = [("a", [1, 2, 3])]
            }
            to be equal to
            {
              [("a", "b")] = [("a", [1, 2, 3])]
            }
            Values differed at keys {a=b}
         """.trimIndent()
      }
   }

})
