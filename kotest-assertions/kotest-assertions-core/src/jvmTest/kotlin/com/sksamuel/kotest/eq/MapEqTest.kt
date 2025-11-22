package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.MapEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MapEqTest : FunSpec({
   test("should give null for simple equal maps") {
      val equals = MapEq.equals(emptyMap<Any, Any>(), emptyMap<Any, Any>(), false)

      equals.shouldBeNull()
   }

   test("should give error for simple not equal maps") {
      val map1 = mapOf("a" to "actual")
      val map2 = mapOf("a" to "expected")

      val throwable = MapEq.equals(map1, map2, false)

      assertSoftly {
         throwable.shouldBeInstanceOf<AssertionError>()
         throwable.message shouldBe """
            Values differed at keys a
            expected:<{
              "a" = "expected"
            }> but was:<{
              "a" = "actual"
            }>
         """.trimIndent()
      }
   }

   test("should give null for complex equal maps") {
      val map1 = mapOf("a" to arrayOf(1, 2))
      val map2 = mapOf("a" to arrayOf(1, 2))

      MapEq.equals(map1, map2, false).shouldBeNull()
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

      MapEq.equals(actual, expected, false).shouldBeNull()
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

      val throwable = MapEq.equals(actual, expected, false)
      assertSoftly {
         throwable.shouldNotBeNull()
         throwable.message shouldBe """
            Values differed at keys 1
            expected:<{
              "1" = [("2", [("3", "bar")])]
            }> but was:<{
              "1" = [("2", [("3", [("4", ["foo"])])])]
            }>
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
      MapEq.equals(map1, map2, false).shouldBeNull()
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
      val throwable = MapEq.equals(map1, map2, false)
      assertSoftly {
         throwable.shouldNotBeNull()
         throwable.message shouldBe """
            Values differed at keys {a=b}
            expected:<{
              [("a", "c")] = [("a", [1, 2, 3])]
            }> but was:<{
              [("a", "b")] = [("a", [1, 2, 3])]
            }>
         """.trimIndent()
      }
   }


   test("should compare complex structures") {
      val complexStructure = mapOf(
         "array" to arrayOf(1, 2),
         "list" to listOf(1, 2, 3),
         "bytearray" to byteArrayOf(1, 2, 3, 4),
         "int" to 2,
         "string" to "sss",
         "map" to mapOf(
            "2" to 2,
            "list" to listOf("1", 2, "5", null, mapOf("1" to byteArrayOf(1, 2, 3, 5)), setOf(listOf(1, 2, 3))),
            "bytearray" to byteArrayOf(1, 2, 3, 5)
         ),
         "null" to null
      )

      val complexStructureCopy = mapOf(
         "array" to arrayOf(1, 2),
         "list" to listOf(1, 2, 3),
         "bytearray" to byteArrayOf(1, 2, 3, 4),
         "int" to 2,
         "string" to "sss",
         "map" to mapOf(
            "2" to 2,
            "list" to listOf("1", 2, "5", null, mapOf("1" to byteArrayOf(1, 2, 3, 5)), setOf(listOf(1, 2, 3))),
            "bytearray" to byteArrayOf(1, 2, 3, 5)
         ),
         "null" to null
      )

      complexStructure shouldBe complexStructureCopy
   }

   test("should handle cyclic maps without StackOverflowError") {
      // Create a self-referential map
      val cyclicMap = mutableMapOf<String, Any?>()
      cyclicMap["self"] = cyclicMap

      // Comparing a cyclic map with itself should work (same instance)
      MapEq.equals(cyclicMap, cyclicMap, false).shouldBeNull()
   }

   test("should handle cyclic maps in nested structures") {
      // Create a self-referential map
      val cyclicMap = mutableMapOf<String, Any?>()
      cyclicMap["b"] = cyclicMap

      // Put it inside a container map
      val container = mapOf("foo" to mapOf("bar" to "baz", "baz" to cyclicMap))

      // This should not overflow when comparing the cyclic map with itself
      val extracted = (container["foo"] as Map<*, *>)["baz"]
      MapEq.equals(extracted as Map<*, *>, cyclicMap, false).shouldBeNull()
   }

})
