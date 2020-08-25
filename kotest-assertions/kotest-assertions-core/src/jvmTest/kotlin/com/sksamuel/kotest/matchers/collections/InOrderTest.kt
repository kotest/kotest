package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containsInOrder
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage

class InOrderTest : WordSpec() {
   init {
      "containInOrder" should {
         "test that a collection contains the same elements in the given order, duplicates permitted" {
            val col = listOf(1, 1, 2, 2, 3, 3)

            col should containsInOrder(1, 2, 3)
            col should containsInOrder(1)

            shouldThrow<AssertionError> {
               col should containsInOrder(1, 2, 6)
            }

            shouldThrow<AssertionError> {
               col should containsInOrder(4)
            }

            shouldThrow<AssertionError> {
               col should containsInOrder(2, 1, 3)
            }
         }
         "work with unsorted collections" {
            val actual = listOf(5, 3, 1, 2, 4, 2)
            actual should containsInOrder(3, 2, 2)
         }
         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf<Number>(1L, 2L) should containsInOrder(listOf<Number>(1, 2))
            }.shouldHaveMessage("""[
  1L,
  2L
] did not contain the elements [
  1,
  2
] in order""")
         }
      }
   }
}
