package io.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.throwable.shouldHaveMessage

class LargerTest : FunSpec({

   context("Should Be Larger Than") {
      context("Iterables") {
         test("Iterable of size 3 should be larger than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            col1 shouldBeLargerThan col2
         }

         test("Iterable of size 3 should fail to be larger than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            shouldThrow<AssertionError> {
               col2 shouldBeLargerThan col1
            } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
         }
      }

      context("Arrays") {
         test("Array of size 3 should be larger than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            arr1 shouldBeLargerThan arr2
         }

         test("Array of size 3 should fail to be larger than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            shouldThrow<AssertionError> {
               arr2 shouldBeLargerThan arr1
            } shouldHaveMessage "Collection of size 2 should be larger than collection of size 3"
         }
      }
   }

   context("Should NOT Be Larger Than") {
      context("Iterables") {
         test("Iterable of size 2 should not be larger than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            col1 shouldNotBeLargerThan col2
         }

         test("Iterable of size 2 should fail to not be larger than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            shouldThrow<AssertionError> {
               col2 shouldNotBeLargerThan col1
            } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
         }
      }

      context("Arrays") {
         test("Array of size 2 should not be larger than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            arr1 shouldNotBeLargerThan arr2
         }

         test("Array of size 2 should fail to not be larger than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            shouldThrow<AssertionError> {
               arr2 shouldNotBeLargerThan arr1
            } shouldHaveMessage "Collection of size 3 should not be larger than collection of size 2"
         }
      }
   }

   context("beLargerThan Matchers") {
      test("passes when the collection is larger") {
         val larger = listOf(1, 2, 3)
         val smaller = listOf(1, 2)
         larger should beLargerThan(smaller)
      }

      test("fails when the collection is not larger") {
         val smaller = listOf(1, 2)
         val larger = listOf(1, 2, 3)
         smaller shouldNot beLargerThan(larger)
      }

      test("fails when the collections are of the same size") {
         val collection1 = listOf(1, 2, 3)
         val collection2 = listOf(4, 5, 6)
         collection1 shouldNot beLargerThan(collection2)
      }

      test("works with empty collections") {
         val emptyCollection = emptyList<Int>()
         val nonEmptyCollection = listOf(1)
         nonEmptyCollection should beLargerThan(emptyCollection)
         emptyCollection shouldNot beLargerThan(nonEmptyCollection)
      }

      test("throws no exceptions with empty collections compared") {
         val empty1 = emptyList<Int>()
         val empty2 = emptyList<Int>()
         empty1 shouldNot beLargerThan(empty2)
      }
   }
})
