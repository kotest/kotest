package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldExistInOrder
import io.kotest.matchers.string.shouldContainInOrder

class ShouldExistInOrderTest: WordSpec() {
   init {
       "shouldExistInOrder" should {
          "pass" {
              listOf(1, 2).shouldExistInOrder(
                 { i: Int -> i < 2 },
                 { i: Int -> i < 3 }
              )
          }
          "fail with clear message" {
             shouldThrowAny {
                listOf(1, 2).shouldExistInOrder(
                   { i: Int -> i < 2 },
                   { i: Int -> i < 2 }
                )
             }.message.shouldContainInOrder(
                "[1, 2] did not match the predicates in order. Predicate at index 1 did not match.",
                "but found element(s) matching the predicate out of order at index(es): [0]",
             )
          }
       }
   }
}
