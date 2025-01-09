package io.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class SmallerTest : FunSpec({

   context("Should Be Smaller Than") {
      context("Iterables") {
         test("Iterable of size 2 should be smaller than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            col1 shouldBeSmallerThan col2
         }

         test("Iterable of size 3 should fail to be smaller than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            shouldThrow<AssertionError> {
               col1 shouldBeSmallerThan col2
            } shouldHaveMessage "Collection of size 3 should be smaller than collection of size 2"
         }
      }

      context("Arrays") {
         test("Array of size 2 should be smaller than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            arr1 shouldBeSmallerThan arr2
         }

         test("Array of size 3 should fail to be smaller than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            shouldThrow<AssertionError> {
               arr1 shouldBeSmallerThan arr2
            } shouldHaveMessage "Collection of size 3 should be smaller than collection of size 2"
         }
      }
   }

   context("Should NOT Be Smaller Than") {

      context("Iterables") {
         test("Iterable of size 3 should not be smaller than iterable of size 2") {
            val col1 = listOf(1, 2, 3)
            val col2 = listOf(1, 2)
            col1 shouldNotBeSmallerThan col2
         }

         test("Iterable of size 2 should fail to not be smaller than iterable of size 3") {
            val col1 = listOf(1, 2)
            val col2 = listOf(1, 2, 3)
            shouldThrow<AssertionError> {
               col1 shouldNotBeSmallerThan col2
            } shouldHaveMessage "Collection of size 2 should not be smaller than collection of size 3"
         }
      }

      context("Arrays") {
         test("Array of size 3 should not be smaller than array of size 2") {
            val arr1 = arrayOf(1, 2, 3)
            val arr2 = arrayOf(1, 2)
            arr1 shouldNotBeSmallerThan arr2
         }

         test("Array of size 2 should fail to not be smaller than array of size 3") {
            val arr1 = arrayOf(1, 2)
            val arr2 = arrayOf(1, 2, 3)
            shouldThrow<AssertionError> {
               arr1 shouldNotBeSmallerThan arr2
            } shouldHaveMessage "Collection of size 2 should not be smaller than collection of size 3"
         }
      }
   }

})
