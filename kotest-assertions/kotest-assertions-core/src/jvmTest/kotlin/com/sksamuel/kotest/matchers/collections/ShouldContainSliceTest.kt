package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainSlice
import io.kotest.matchers.collections.shouldNotContainSlice
import io.kotest.matchers.string.shouldContainInOrder

class ShouldContainSliceTest : StringSpec() {
   init {
      "pass when slice is at the beginning" {
         listOf(1, 2, 3, 4, 5).shouldContainSlice(listOf(1, 2, 3))
         shouldThrow<AssertionError> {
            listOf(1, 2, 3, 4, 5).shouldNotContainSlice(listOf(1, 2, 3))
         }
      }
      "pass when slice is in the middle" {
         listOf(1, 2, 3, 4, 5).shouldContainSlice(listOf(2, 3, 4))
         shouldThrow<AssertionError> {
            listOf(1, 2, 3, 4, 5).shouldNotContainSlice(listOf(2, 3, 4))
         }
      }
      "pass when slice is at the end" {
         listOf(1, 2, 3, 4, 5).shouldContainSlice(listOf(3, 4, 5))
         shouldThrow<AssertionError> {
            listOf(1, 2, 3, 4, 5).shouldNotContainSlice(listOf(3, 4, 5))
         }
      }
      "fail when slice not found" {
         listOf(1, 2, 3, 4, 5).shouldNotContainSlice(listOf(1, 2, 4, 5))
         val thrown = shouldThrow<AssertionError> {
            listOf(1, 2, 3, 4, 5).shouldContainSlice(listOf(1, 2, 4, 5))
         }
         thrown.message.shouldContainInOrder(
            "List should contain slice [1, 2, 4, 5] but was [1, 2, 3, 4, 5]",
            "Slice[0] of expected with indexes: 0..1 matched a slice of actual values with indexes: 0..1",
            "Slice[1] of expected with indexes: 2..3 matched a slice of actual values with indexes: 3..4",
            "[0] 1 => slice 0",
            "[1] 2 => slice 0",
            "[3] 4 => slice 1",
            "[4] 5 => slice 1",
         )
      }
   }
}
