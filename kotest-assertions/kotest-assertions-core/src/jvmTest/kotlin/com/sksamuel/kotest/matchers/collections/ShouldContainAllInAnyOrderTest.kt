package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldNotContainAllInAnyOrder
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainAllInAnyOrderTest : FunSpec({

   context("containAllInAnyOrder") {

      test("should succeed for empty with empty") {
         val empty = emptyList<Int>()
         empty.shouldContainAllInAnyOrder(emptyList())
      }

      test("should fail for empty with any other") {
         val empty = emptyList<Int>()
         shouldThrow<AssertionError> {
            empty.shouldContainAllInAnyOrder(listOf(null, null, null, null))
         }
      }

      test("should succeed when elements are same") {
         val countdown = (10 downTo 0).toList()
         val countup = (0..10).toList()
         countdown.shouldContainAllInAnyOrder(countup)
      }

      test("should succeed when LHS is a bigger list") {
         val biggerList = (0..15).toList()
         val smallerList = (0..10).toList()
         biggerList.shouldContainAllInAnyOrder(smallerList)
      }

      test("should fail for overlapping collection") {
         val countup = (0..10).toList()
         shouldThrow<AssertionError> {
            countup.shouldContainAllInAnyOrder((5..15).toList())
         }
      }

      test("should fail for subset, same count with nulls") {
         val sparse = listOf(null, null, null, 3)
         val nulls = listOf<Int?>(null, null, null, null)
         shouldThrow<AssertionError> {
            sparse.shouldContainAllInAnyOrder(nulls)
         }
      }

      test("should succeed for subset, same count") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         val unique = listOf(3, 2, 1)
         repeating.shouldContainAllInAnyOrder(unique + unique)
      }

      test("should succeed for subset, same count (variadic)") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         repeating.shouldContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
      }

      test("should not fail for subset, different count with nulls") {
         val sparse = listOf(null, null, null, 3)
         sparse.shouldContainAllInAnyOrder(sparse.toSet().toList())
      }

      test("should not fail for same elements, smaller count") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         val unique = listOf(3, 2, 1)
         repeating.shouldContainAllInAnyOrder(unique)
      }

      test("should fail for same elements, bigger count") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         val unique = listOf(3, 2, 1)
         shouldThrow<AssertionError> {
            unique.shouldContainAllInAnyOrder(repeating)
         }
      }

      test("should detect only bigger count of individual elements in collections of same length") {
         shouldThrowAny {
            listOf(1, 2, 2).shouldContainAllInAnyOrder(listOf(1, 1, 2))
         }.shouldHaveMessage("""
            |Collection should contain the values of [1, 1, 2] in any order, but was [1, 2, 2].
            |Count Mismatches:
            |  For 1: expected count: <2>, but was: <1>
            """.trimMargin())
      }
   }

   context("notContainAllInAnyOrder") {

      test("should fail for empty with empty") {
         val empty = emptyList<Int>()
         shouldThrow<AssertionError> {
            empty.shouldNotContainAllInAnyOrder(emptyList())
         }
      }

      test("should succeed for empty with any other") {
         val empty = emptyList<Int>()
         val nulls = listOf<Int?>(null, null, null, null)
         empty.shouldNotContainAllInAnyOrder(nulls)
      }

      test("should fail when elements are same") {
         val countdown = (10 downTo 0).toList()
         val countup = (0..10).toList()
         shouldThrow<AssertionError> {
            countdown.shouldNotContainAllInAnyOrder(countup)
         }
      }

      test("should succeed for overlapping collection") {
         val countup = (0..10).toList()
         countup.shouldNotContainAllInAnyOrder((5..15).toList())
      }

      test("should fail for subset, same count with nulls") {
         val sparse = listOf(null, null, null, 3)
         val nulls = listOf<Int?>(null, null, null)
         shouldThrow<AssertionError> {
            sparse.shouldNotContainAllInAnyOrder(nulls)
         }
      }

      test("should fail for subset, same count") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         val unique = listOf(3, 2, 1)
         shouldThrow<AssertionError> {
            repeating.shouldNotContainAllInAnyOrder(unique + unique)
         }
      }

      test("should fail for subset, same count (variadic)") {
         val repeating = listOf(1, 2, 3, 1, 2, 3)
         shouldThrow<AssertionError> {
            repeating.shouldNotContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
         }
      }

      test("should fail for subset, different count with nulls") {
         val sparse = listOf(null, null, null, 3)
         shouldThrow<AssertionError> {
            sparse.shouldNotContainAllInAnyOrder(sparse.toSet().toList())
         }
      }

      test("should succeed for same, bigger count for some elements") {
         val repeating = listOf(1, 2, 3, 1)
         val unique = listOf(3, 2, 1)
         unique.shouldNotContainAllInAnyOrder(repeating)
      }

      test("should succeed detect different count of individual elements in collections of same length") {
         listOf(1, 2, 2).shouldNotContainAllInAnyOrder(listOf(1, 1, 2))
      }
   }
})
