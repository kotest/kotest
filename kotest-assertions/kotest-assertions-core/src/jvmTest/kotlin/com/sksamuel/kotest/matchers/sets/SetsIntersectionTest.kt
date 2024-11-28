package com.sksamuel.kotest.matchers.sets

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.sets.shouldIntersect
import io.kotest.matchers.sets.shouldNotIntersect
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder

class SetsIntersectionTest: WordSpec() {
  init {
    "shouldIntersect" should {
      "test intersection" {
        val set1 = setOf(1, 2, 3, 4, 5)
        val set2 = setOf(4, 5, 6, 7, 8)
        set1 shouldIntersect set2
      }
    }
    "shouldNotIntersect" should {
      "test intersection" {
        val set1 = setOf(1, 2, 3)
        val set2 = setOf(4, 5, 6)
        set1 shouldNotIntersect set2
      }
       "print clear failure message" {
         val set1 = setOf(1, 2, 3)
         val set2 = setOf(4, 5, 6, 2)
          val message = shouldThrow<AssertionError> {
            set1 shouldNotIntersect set2
          }.message
            message.shouldContainInOrder(
               "should not intersect with [4, 5, 6, 2]",
               "but had the following common element(s): [2]"
            )
       }
    }
  }
}
