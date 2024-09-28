package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveLowerBound
import io.kotest.matchers.collections.shouldHaveUpperBound
import io.kotest.matchers.throwable.shouldHaveMessage

class BoundsTest : WordSpec() {
   init {
      "haveUpperBound" should {
         "pass" {
            listOf(1, 2, 3) shouldHaveUpperBound 3
            listOf(1, 2, 3) shouldHaveUpperBound 4
         }

         fun msg(name: String) = "$name should have upper bound 2, but the following elements are above it: [3]"

         "fail for Array" {
            shouldThrowAny { arrayOf(1, 2, 3) shouldHaveUpperBound 2 }.shouldHaveMessage(msg("Array"))
         }

         "fail for List" {
            shouldThrowAny { listOf(1, 2, 3) shouldHaveUpperBound 2 }.shouldHaveMessage(msg("List"))
         }

         "fail for Set" {
            shouldThrowAny { setOf(1, 2, 3) shouldHaveUpperBound 2 }.shouldHaveMessage(msg("Set"))
         }
      }

      "haveLowerBound" should {
         "pass" {
            listOf(1, 2, 3) shouldHaveLowerBound 1
            listOf(1, 2, 3) shouldHaveLowerBound 0
         }
         "fail" {
            shouldThrowAny {
               listOf(1, 2, 3) shouldHaveLowerBound 2
            }.shouldHaveMessage("Collection should have lower bound 2, but the following elements are below it: [1]")
         }
      }
   }
}
