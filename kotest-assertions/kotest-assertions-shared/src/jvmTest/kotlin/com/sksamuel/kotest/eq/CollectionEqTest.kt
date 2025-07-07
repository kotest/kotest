package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.CollectionEq
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.TreeSet
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class CollectionEqTest : FunSpec({
   test("Comparing empty set with other collection should be ok") {
      shouldNotThrowAny {
         emptySet<Int>() shouldBe listOf()
      }
   }

   test("Comparing empty set with other non empty collection should provide meaningful assertion error") {
      shouldFail {
         emptySet<Int>() shouldBe listOf(1)
      }.message shouldBe """
         Missing elements from index 0
         expected:<[1]> but was:<[]>
      """.trimIndent()
   }

   test("should give null for two equal sets") {
      CollectionEq.equals(setOf(1, 2, 3), setOf(2, 3, 1), false).shouldBeNull()
   }

   test("should give error for unequal sets") {
      val error = CollectionEq.equals(setOf(1, 2, 3), setOf(2, 3), false)

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe "expected:<[2, 3]> but was:<[1, 2, 3]>"
      }
   }

   test("should give null for two equal list") {
      CollectionEq.equals(listOf(1, 2, 3), listOf(1, 2, 3), false).shouldBeNull()
   }

   test("should give error for two unequal list") {
      val error = CollectionEq.equals(listOf(3), listOf(1, 2, 3), false)

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [0]
                                  |Missing elements from index 1
                                  |expected:<[1, 2, 3]> but was:<[3]>""".trimMargin()
      }
   }

   test("should not give error for kotlin ordered set comparison with list") {
      CollectionEq.equals(setOf(1, 2, 3), listOf(1, 2, 3), false).shouldBeNull()
   }

   test("should give error for unordered set comparison with list") {
      val hs = HashSet<Int>(3)
      hs.addAll(setOf(1, 2, 3))
      val error = CollectionEq.equals(hs, listOf(1, 2, 3), false)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Disallowed: Sets can only be compared to sets, unless both types provide a stable iteration order.
                                  |HashSet does not provide a stable iteration order and was compared with ArrayList which is not a Set""".trimMargin()
      }
   }

   test("should give null for java-only TreeSet comparison with list") {
      val hs = TreeSet(setOf(1, 2, 3))
      CollectionEq.equals(hs, listOf(1, 2, 3), false).shouldBeNull()
   }

   test("should give null for equal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      CollectionEq.equals(array1.toList(), array2.toList(), false).shouldBeNull()
   }

   test("should give error for equal iterables nested in ordered collection") {
      val aut1 = listOf(1, arrayOf(listOf(3, 3), listOf(4, 4)), mapOf("a" to arrayOf(1, 2, 3)))
      val aut2 = listOf(1, arrayOf(listOf(3, 3), listOf(4, 4)), mapOf("a" to arrayOf(1, 2, 3)))
      val error = CollectionEq.equals(aut1, aut2, false)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for unequal iterables nested in ordered collection") {
      val aut1 = listOf(1, listOf(listOf(3, 3), listOf(3, 4)), mapOf("a" to listOf(1, 2, 3)))
      val aut2 = listOf(1, listOf(listOf(3, 3), listOf(4, 4)), mapOf("a" to listOf(1, 2, 3)))

      val error = CollectionEq.equals(aut1, aut2, false)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for equal collections nested in Set") {
      val aut1 = setOf(1, listOf(listOf(3, 3), listOf(4, 4)), mapOf("a" to arrayOf(1, 2, 3)))
      val aut2 = setOf(mapOf("a" to listOf(1, 2, 3)), 1, arrayOf(listOf(3, 3), listOf(4, 4)))
      val error = CollectionEq.equals(aut1, aut2, false)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[[("a", [1, 2, 3])], 1, [[], []]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>"""
      }
   }

   test("should give error for unequal collections nested in Set") {
      val aut1 = setOf(listOf(listOf(3, 3), listOf(3, 4)), mapOf("a" to listOf(1, 2, 3)), 1)
      val aut2 = setOf(1, listOf(listOf(3, 3), listOf(4, 4)), mapOf("a" to listOf(1, 2, 3)))

      val error = CollectionEq.equals(aut1, aut2, false)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[[[], []], [("a", [1, 2, 3])], 1]>"""
      }
   }

   test("should give error for unequal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "e"), "b"), mapOf("a" to arrayOf(1, 2, 3)))

      val error = CollectionEq.equals(array1.toList(), array2.toList(), false)

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [["a", "e"], "b"], [("a", [1, 2, 3])]]> but was:<[1, [["a", "c"], "b"], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should return true for deeply nested arrays in sets") {
      setOf(
         arrayOf(1, 2, 3),
         1,
         listOf(arrayOf(1, 2, 3))
      ) shouldBe setOf(
         arrayOf(1, 2, 3),
         1,
         listOf(arrayOf(1, 2, 3))
      )
   }

   test("should have linear performance for lists").config(timeout = 5.seconds) {
      val a = List(10000000) { "foo" }
      val b = List(10000000) { "foo" }
      CollectionEq.equals(a, b, false) shouldBe null
   }

   test("should have linear performance for primitive sets").config(timeout = 5.seconds) {
      CollectionEq.equals(List(1000) { it }.toSet(), List(1000) { it }.reversed().toSet(), false) shouldBe null
   }

   test("should have linear performance for string sets").config(timeout = 5.seconds) {
      CollectionEq.equals(
         List(1000) { it.toString() }.toSet(),
         List(1000) { it.toString() }.reversed().toSet(),
         false,
      ) shouldBe null
   }

   test("should work for empty lists") {
      val errorMessage1 = CollectionEq.equals(emptyList<Int>(), listOf(1), false)?.message
      errorMessage1 shouldBe """Missing elements from index 0
                               |expected:<[1]> but was:<[]>""".trimMargin()

      val errorMessage2 = CollectionEq.equals(listOf(1, 2), emptyList<Int>(), false)?.message
      errorMessage2 shouldBe """Unexpected elements from index 1
                               |expected:<[]> but was:<[1, 2]>""".trimMargin()
   }

   test("shouldNotBe should work for empty lists") {
      listOf("hello") shouldNotBe emptyList<String>()
      emptyList<String>() shouldNotBe listOf("hello")
   }

   test("CollectionEq should not support sequence") {
      shouldFail {
         sequenceOf(1, 2) shouldBe listOf(1, 2)
      }
   }
})
