package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.IterableEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.seconds

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
         error.message shouldBe "Elements differ at index 0: expected:<1> but was:<3>"
      }
   }

   test("should give null for equal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      IterableEq.equals(array1.toList(), array2.toList()).shouldBeNull()
   }

   test("should give error for unequal deeply nested arrays") {
      val array1 = arrayOf(1, arrayOf(arrayOf("a", "c"), "b"), mapOf("a" to arrayOf(1, 2, 3)))
      val array2 = arrayOf(1, arrayOf(arrayOf("a", "e"), "b"), mapOf("a" to arrayOf(1, 2, 3)))

      val error = IterableEq.equals(array1.toList(), array2.toList())

      assertSoftly {
         error.shouldNotBeNull()
         error.message shouldBe """Elements differ at index 1: expected:<[["a", "e"], "b"]> but was:<[["a", "c"], "b"]>"""
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
      IterableEq.equals(emptyList<Int>(), listOf(1))?.message shouldBe
         "Element 1 expected at index 0 but there were no further elements"
      IterableEq.equals(listOf(1), emptyList<Int>())?.message shouldBe "Unexpected element at index 0: 1"
   }

   test("shouldNotBe should work for empty lists") {
      listOf("hello") shouldNotBe emptyList<String>()
      emptyList<String>() shouldNotBe listOf("hello")
   }

   test("IterableEq unsupported types") {
      listOf(
         Paths.get("foo/bar")
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
