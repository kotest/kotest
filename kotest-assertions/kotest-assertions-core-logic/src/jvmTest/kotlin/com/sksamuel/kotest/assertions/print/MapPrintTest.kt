package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.MapPrint
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class MapPrintTest : FunSpec() {
   init {

      test("MapPrint should handle maps") {
         MapPrint.print(mapOf("foo" to "a", "bar" to 33L)) shouldBe Printed("""[("foo", "a"), ("bar", 33L)]""")
      }

      test("detect should handle maps") {
         mapOf("foo" to 'c', "bar" to true).print() shouldBe Printed("""[("foo", 'c'), ("bar", true)]""")
      }

      context("cycle detection") {
         test("should handle direct self-referential map without StackOverflowError") {
            val cyclicMap = mutableMapOf<String, Any?>()
            cyclicMap["self"] = cyclicMap

            shouldNotThrowAny {
               val printed = MapPrint.print(cyclicMap).value
               printed shouldContain "(this LinkedHashMap)"
            }
         }

         test("should handle indirect cyclic maps without StackOverflowError") {
            val map1 = mutableMapOf<String, Any?>()
            val map2 = mutableMapOf<String, Any?>()
            map1["ref"] = map2
            map2["ref"] = map1

            shouldNotThrowAny {
               val printed = MapPrint.print(map1).value
               printed shouldContain "(this LinkedHashMap)"
            }
         }

         test("should handle deeply nested cyclic map structures") {
            val map1 = mutableMapOf<String, Any?>()
            val map2 = mutableMapOf<String, Any?>()
            val map3 = mutableMapOf<String, Any?>()
            map1["next"] = map2
            map2["next"] = map3
            map3["next"] = map1 // Creates a cycle: map1 -> map2 -> map3 -> map1

            shouldNotThrowAny {
               val printed = MapPrint.print(map1).value
               printed shouldContain "(this LinkedHashMap)"
            }
         }

         test("should handle unequal indirect cyclic maps without StackOverflowError") {
            val map1 = mutableMapOf<String, Any?>()
            val map2 = mutableMapOf<String, Any?>()
            map1["ref"] = map2
            map1["extra"] = "value"
            map2["ref"] = map1

            shouldNotThrowAny {
               val printed = MapPrint.print(map1).value
               printed shouldContain "(this LinkedHashMap)"
               printed shouldContain "extra"
            }
         }

         test("should handle map with cyclic list as value without StackOverflowError") {
            val cyclicList = mutableListOf<Any?>()
            cyclicList.add(cyclicList)
            val map = mapOf("list" to cyclicList)

            shouldNotThrowAny {
               val printed = MapPrint.print(map).value
               printed shouldContain "(this ArrayList)"
            }
         }
      }
   }
}
