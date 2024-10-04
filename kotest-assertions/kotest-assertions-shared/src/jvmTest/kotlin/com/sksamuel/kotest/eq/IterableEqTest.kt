package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.IterableEq
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.nio.file.Paths
import java.util.TreeSet
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashSet
import kotlin.time.Duration.Companion.seconds

private class BareIterable(size: Int, offset: Int): Iterable<Int> {
   val top = offset+size
   private var count = offset
   override fun iterator() = object : Iterator<Int> {
      override fun hasNext(): Boolean = count < top
      override fun next(): Int = ++count
   }
   override fun toString(): String = "${this::class.simpleName}@{$top,$count}"
}

private class BareRecursiveIterable(size: Int, offset: Int): Iterable<BareRecursiveIterable> {
   val top = offset+size
   private var count = offset
   override fun iterator() = object : Iterator<BareRecursiveIterable> {
      override fun hasNext(): Boolean = count < top
      override fun next(): BareRecursiveIterable = this@BareRecursiveIterable.apply { ++count }
   }
   override fun toString(): String = "${this::class.simpleName}@{$top,$count}"
}

private val expectedPath = if (System.getProperty("os.name").lowercase().contains("win")) {
   "WindowsPath"
} else {
   "UnixPath"
}

@EnabledIf(LinuxCondition::class)
class IterableEqTest : FunSpec({
   test("Comparing empty set with other iterable should be ok") {
      shouldNotThrowAny {
         emptySet<Int>() shouldBe listOf()
      }
   }

   test("Comparing empty set with other iterable should provide meaningful assertion error") {
      shouldFail {
         emptySet<Int>() shouldBe listOf(1)
      }.message shouldBe """
         Missing elements from index 0
         expected:<[1]> but was:<[]>
      """.trimIndent()
   }

   test("should give null for two equal sets") {
      IterableEq.equals(setOf(1, 2, 3), setOf(2, 3, 1)).shouldBeNull()
   }

   test("should give error for unequal sets") {
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
         error.message shouldBe """Element differ at index: [0]
                                  |Missing elements from index 1
                                  |expected:<[1, 2, 3]> but was:<[3]>""".trimMargin()
      }
   }

   test("should not give error for kotlin ordered set comparison with list") {
      IterableEq.equals(setOf(1, 2, 3), listOf(1, 2, 3)).shouldBeNull()
   }

   test("should give error for unordered set comparison with list") {
      val hs = HashSet<Int>(3)
      hs.addAll(setOf(1, 2, 3))
      val error = IterableEq.equals(hs, listOf(1, 2, 3))
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Disallowed: Sets can only be compared to sets, unless both types provide a stable iteration order.
                                  |HashSet does not provide a stable iteration order and was compared with ArrayList which is not a Set""".trimMargin()
      }
   }

   test("should give null for java-only TreeSet comparison with list") {
      val hs = TreeSet(setOf(1, 2, 3))
      IterableEq.equals(hs, listOf(1, 2, 3)).shouldBeNull()
   }

   test("should give error for unmatched collection") {
      val error = IterableEq.equals(BareIterable(3,1), listOf(1,2,3))
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Disallowed typed contract
                                  |May not compare BareIterable with ArrayList
                                  |""".trimMargin()
      }
   }

   test("should give null for matched iterables") {
      IterableEq.equals(Paths.get("foo"), Paths.get("foo")).shouldBeNull()
      IterableEq.equals(BareIterable(3,2), BareIterable(3,2)).shouldBeNull()
   }

   test("should give error for matched but different iterables") {
      val error = IterableEq.equals(BareIterable(2,2), BareIterable(3,2))
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Missing elements from index 2
                                  |expected:<[5]> but was:<[]>""".trimMargin()
      }
   }

   test("should give error for promiscuous iterables") {
      val error = IterableEq.equals(Paths.get("foo"), BareIterable(1,0))
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Disallowed promiscuous iterators
                                  |May not compare $expectedPath with BareIterable
                                  |""".trimMargin()
      }
   }

   test("should give error for promiscuous iterables when recursive") {
      val error = IterableEq.equals(Paths.get("foo"), BareRecursiveIterable(1,0))
      System.getProperty("os")
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Disallowed promiscuous iterators
                                  |May not compare $expectedPath with BareRecursiveIterable
                                  |""".trimMargin()
      }
   }

   test("should give unsupported error for nested iterables") {
      val error = IterableEq.equals(Paths.get("foo"), Paths.get("bar"))
      assertSoftly {
         error.shouldNotBeNull()
         error.message?.startsWith("Disallowed nesting iterator") shouldBe true
         error.message?.endsWith("; (use custom test code instead)") shouldBe true
      }
   }

   test("should give unsupported error for recursive iterables") {
      val error = IterableEq.equals(BareRecursiveIterable(1,1), BareRecursiveIterable(1,1))
      assertSoftly {
         error.shouldNotBeNull()
         error.message?.startsWith("Disallowed nesting iterator") shouldBe true
         error.message?.endsWith("; (use custom test code instead)") shouldBe true
      }
   }

   test("should give null for equal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      IterableEq.equals(array1.toList(), array2.toList()).shouldBeNull()
   }

   test("should give error for equal iterables nested in ordered collection") {
      val aut1 = listOf(1, arrayOf(BareIterable(3,3), BareIterable(4,4)), mapOf("a" to arrayOf(1, 2, 3)))
      val aut2 = listOf(1, arrayOf(BareIterable(3,3), BareIterable(4,4)), mapOf("a" to arrayOf(1, 2, 3)))
      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for unequal iterables nested in ordered collection") {
      val aut1 = listOf(1, listOf(BareIterable(3,3), BareIterable(3,4)), mapOf("a" to listOf(1, 2, 3)))
      val aut2 = listOf(1, listOf(BareIterable(3,3), BareIterable(4,4)), mapOf("a" to listOf(1, 2, 3)))

      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for equal iterables nested in Set") {
      val aut1 = setOf(1, listOf(BareIterable(3,3), BareIterable(4,4)), mapOf("a" to arrayOf(1, 2, 3)))
      val aut2 = setOf(mapOf("a" to listOf(1, 2, 3)), 1, arrayOf(BareIterable(3,3), BareIterable(4,4)))
      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[[("a", [1, 2, 3])], 1, [[], []]]> but was:<[1, [[], []], [("a", [1, 2, 3])]]>"""
      }
   }

   test("should give error for unequal iterables nested in Set") {
      val aut1 = setOf(listOf(BareIterable(3,3), BareIterable(3,4)), mapOf("a" to listOf(1, 2, 3)), 1)
      val aut2 = setOf(1, listOf(BareIterable(3,3), BareIterable(4,4)), mapOf("a" to listOf(1, 2, 3)))

      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[1, [[], []], [("a", [1, 2, 3])]]> but was:<[[[], []], [("a", [1, 2, 3])], 1]>"""
      }
   }

   test("should give error for unequal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "e"), "b"), mapOf("a" to arrayOf(1, 2, 3)))

      val error = IterableEq.equals(array1.toList(), array2.toList())

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [["a", "e"], "b"], [("a", [1, 2, 3])]]> but was:<[1, [["a", "c"], "b"], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for recursive iterables nested in ordered collection") {
      val aut1 = listOf(1, listOf(BareRecursiveIterable(3,3)), mapOf("a" to listOf(1, 2, 3)))
      val aut2 = listOf(1, listOf(BareRecursiveIterable(3,3)), mapOf("a" to listOf(1, 2, 3)))

      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Element differ at index: [1]
                                  |expected:<[1, [[]], [("a", [1, 2, 3])]]> but was:<[1, [[]], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should give error for recursive iterables nested in Set") {
      val aut1 = setOf(1, arrayOf(BareRecursiveIterable(3,3)), mapOf("a" to listOf(1, 2, 3)))
      val aut2 = setOf(1, mapOf("a" to listOf(1, 2, 3)), listOf(BareRecursiveIterable(3,3)))

      val error = IterableEq.equals(aut1, aut2)
      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """expected:<[1, [("a", [1, 2, 3])], [[]]]> but was:<[1, [[]], [("a", [1, 2, 3])]]>""".trimMargin()
      }
   }

   test("should only perform one iteration") {

      val actual = IterationCountSet(List(50) { it }.toSet())
      val expected = IterationCountSet(List(50) { it }.toSet())

      actual.count shouldBe 0
      expected.count shouldBe 0

      IterableEq.equals(actual, expected).shouldBeNull()

      actual.count shouldBe 50  // one for each element in the Set
      expected.count shouldBe 0
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
       IterableEq.equals(a, b) shouldBe null
   }

   test("should have linear performance for primitive sets").config(timeout = 5.seconds) {
       IterableEq.equals(List(1000) { it }.toSet(), List(1000) { it }.reversed().toSet()) shouldBe null
   }

   test("should have linear performance for string sets").config(timeout = 5.seconds) {
       IterableEq.equals(
           List(1000) { it.toString() }.toSet(),
           List(1000) { it.toString() }.reversed().toSet()
       ) shouldBe null
   }

   test("should work for empty lists") {
      val errorMessage1 = IterableEq.equals(emptyList<Int>(), listOf(1))?.message
      errorMessage1 shouldBe """Missing elements from index 0
                               |expected:<[1]> but was:<[]>""".trimMargin()

      val errorMessage2 = IterableEq.equals(listOf(1, 2), emptyList<Int>())?.message
      errorMessage2 shouldBe """Unexpected elements from index 1
                               |expected:<[]> but was:<[1, 2]>""".trimMargin()
   }

   test("shouldNotBe should work for empty lists") {
      listOf("hello") shouldNotBe emptyList<String>()
      emptyList<String>() shouldNotBe listOf("hello")
   }

   test("IterableEq unsupported types") {

      listOf(
         sequenceOf(1),
      ).forAll {
         IterableEq.isValidIterable(it) shouldBe false
      }
   }

   test("IterableEq supported types") {

      listOf(
         setOf(1),
         listOf(1),
         java.util.ArrayList(listOf(1)),
         java.util.HashSet(listOf(1)),
         arrayOf(1),
         ConcurrentLinkedQueue<Int>(),
      ).forAll {
         IterableEq.isValidIterable(it) shouldBe true
      }
   }
})

private class IterationCountSet<T>(val delegate: Set<T>) : Set<T> by delegate {

   var count = 0

   override fun iterator(): Iterator<T> {
      return CountingIterator(delegate.iterator())
   }

   inner class CountingIterator(val delegateIterator: Iterator<T>) : Iterator<T> by delegateIterator {
      override fun next(): T {
         count++
         return delegateIterator.next()
      }
   }
}
