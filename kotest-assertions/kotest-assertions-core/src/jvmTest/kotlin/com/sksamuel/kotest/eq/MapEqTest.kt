package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.eq.MapEq
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.text.equals

class MapEqTest : FunSpec({

   test("should pass for simple equal maps") {
      val result = MapEq.equals(emptyMap<Any, Any>(), emptyMap<Any, Any>(), EqContext())
      result.shouldBeInstanceOf<EqResult.Success>()
   }

   test("should give error for simple not equal maps") {
      val map1 = mapOf("a" to "actual")
      val map2 = mapOf("a" to "expected")

      val result = MapEq.equals(map1, map2, EqContext()) as EqResult.Failure
      val throwable = result.error()

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

   test("should pass for complex equal maps") {
      val map1 = mapOf("a" to arrayOf(1, 2))
      val map2 = mapOf("a" to arrayOf(1, 2))

      MapEq.equals(map1, map2, EqContext()).shouldBeInstanceOf<EqResult.Success>()
   }

   test("should pass for deeply nested equal maps") {
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

      MapEq.equals(actual, expected, EqContext()).shouldBeInstanceOf<EqResult.Success>()
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

      val result = MapEq.equals(actual, expected, EqContext()) as EqResult.Failure
      val throwable = result.error()

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

   test("should pass for equal maps having map as keys") {

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
      MapEq.equals(map1, map2, EqContext()).shouldBeInstanceOf<EqResult.Success>()
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
      val result = MapEq.equals(map1, map2, EqContext()) as EqResult.Failure
      val throwable = result.error()

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
      MapEq.equals(cyclicMap, cyclicMap, EqContext()).shouldBeInstanceOf<EqResult.Success>()
   }

   test("should handle cyclic maps in nested structures") {
      // Create a self-referential map
      val cyclicMap = mutableMapOf<String, Any?>()
      cyclicMap["b"] = cyclicMap

      // Put it inside a container map
      val container = mapOf("foo" to mapOf("bar" to "baz", "baz" to cyclicMap))

      // This should not overflow when comparing the cyclic map with itself
      val extracted = (container["foo"] as Map<*, *>)["baz"]
      MapEq.equals(extracted as Map<*, *>, cyclicMap, EqContext(false)).shouldBeInstanceOf<EqResult.Success>()
   }

   test("should handle mutually recursive maps without StackOverflowError") {
      // Create two maps that reference each other
      val map1 = mutableMapOf<String, Any?>()
      val map2 = mutableMapOf<String, Any?>()
      map1["ref"] = map2
      map2["ref"] = map1

      // These two maps have the same structure, so they should be equal
      MapEq.equals(map1, map2, EqContext()).shouldBeInstanceOf<EqResult.Success>()
   }

   test("should not throw StackOverflowError for unequal indirect cyclic maps") {
      val cyclicMap1 = mutableMapOf<String, Any?>()
      val cyclicMap2 = mutableMapOf<String, Any?>()

      cyclicMap1["ref"] = cyclicMap2
      cyclicMap1["extra"] = "value"
      cyclicMap2["ref"] = cyclicMap1

      val result = MapEq.equals(cyclicMap1, cyclicMap2, EqContext()) as EqResult.Failure
      val throwable = result.error()

      assertSoftly {
         throwable.shouldBeInstanceOf<AssertionError>()
         throwable.message shouldBe """
         Values differed at keys ref, extra
         expected:<{
           "ref" = [("ref", [("ref", [("ref", (this LinkedHashMap)), ("extra", "value")])]), ("extra", "value")]
         }> but was:<{
           "ref" = [("ref", [("ref", [("ref", (this LinkedHashMap))]), ("extra", "value")])],
           "extra" = "value"
         }>
      """.trimIndent()
      }
   }

})
