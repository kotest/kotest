package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.endWith
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldNotEndWith
import io.kotest.matchers.collections.shouldNotStartWith
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.collections.startWith
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.throwable.shouldHaveMessage

class StartWithEndWithTest : WordSpec() {
   init {
      "startWith" should {
         "test that a list starts with the given collection" {
            val col = listOf(1, 2, 3, 4, 5)
            col.shouldStartWith(listOf(1))
            col.shouldStartWith(listOf(1, 2))
            col.shouldNotStartWith(listOf(2, 3))
            col.shouldNotStartWith(listOf(4, 5))
            col.shouldNotStartWith(listOf(1, 3))
         }
         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf(1L, 2L) should startWith(listOf(1L, 3L))
            }.message shouldStartWith("""
               |List should start with [1L, 3L] but was [1L, 2L]
               |The following elements failed:
               |  [1] 2L => expected: <3L>, but was: <2L>
            """.trimMargin())
         }
         "find one submatch".config(enabled = true) {
            val message = shouldThrow<AssertionError> {
               listOf(0L, 1L, 2L, 3L) should startWith(listOf(1L, 2L))
            }.message
            assertSoftly {
               message shouldContain "Slice[0] of expected with indexes: 0..1 matched a slice of actual values with indexes: 1..2"
               message shouldContain "[1] 1L => slice 0"
               message shouldContain "[2] 2L => slice 0"
            }
         }
         "find two non-overlapping submatches".config(enabled = true) {
            val message = shouldThrow<AssertionError> {
               listOf(0L, 1L, 2L, 3L, 4L, 5L) should startWith(listOf(1L, 2L, 4L, 5L))
            }.message
            assertSoftly {
               message shouldContain "Slice[0] of expected with indexes: 0..1 matched a slice of actual values with indexes: 1..2"
               message shouldContain "Slice[1] of expected with indexes: 2..3 matched a slice of actual values with indexes: 4..5"
               message shouldContain "[1] 1L => slice 0"
               message shouldContain "[2] 2L => slice 0"
               message shouldContain "[4] 4L => slice 1"
               message shouldContain "[5] 5L => slice 1"
            }
         }
         "find two overlapping submatches".config(enabled = true) {
            val message = shouldThrow<AssertionError> {
               listOf(0L, 1L, 2L, 3L, 4L, 5L) should startWith(listOf(1L, 2L, 3L, 2L, 3L, 4L))
            }.message
            assertSoftly {
               message shouldContain "Slice[0] of expected with indexes: 0..2 matched a slice of actual values with indexes: 1..3"
               message shouldContain "Slice[1] of expected with indexes: 3..5 matched a slice of actual values with indexes: 2..4"
               message shouldContain "[1] 1L => slice 0"
               message shouldContain "[2] 2L => slices: [0, 1]"
               message shouldContain "[3] 3L => slices: [0, 1]"
               message shouldContain "[4] 4L => slice 1"
            }
         }
         "print errors unambiguously when the actual value is empty"  {
            shouldThrow<AssertionError> {
               emptyList<Long>() should startWith(listOf(1L, 3L))
            }.message shouldStartWith("""
               |List should start with [1L, 3L] but was []
               |Actual collection is shorter than expected slice
               """.trimMargin())
         }
      }

      "endWith" should {
         "test that a list ends with the given collection" {
            val col = listOf(1, 2, 3, 4, 5)
            col.shouldEndWith(listOf(5))
            col.shouldEndWith(listOf(4, 5))
            col.shouldNotEndWith(listOf(2, 3))
            col.shouldNotEndWith(listOf(3, 5))
            col.shouldNotEndWith(listOf(1, 2))
         }
         "print errors unambiguously"  {
            val message = shouldThrow<AssertionError> {
               listOf(1L, 2L, 3L, 4L) should endWith(listOf(1L, 3L))
            }.message
            message.shouldStartWith("""
               |List should end with [1L, 3L] but was [3L, 4L]
               |The following elements failed:
               |  [2] 3L => expected: <1L>, but was: <3L>
               |  [3] 4L => expected: <3L>, but was: <4L>
               """.trimMargin())
         }
         "find submatches"  {
            shouldThrow<AssertionError> {
               listOf(1L, 2L, 3L, 4L, 5L, 6L) should endWith(listOf(2L, 3L, 4L))
            }.message.shouldContainInOrder(
               "Slice[0] of expected with indexes: 0..2 matched a slice of actual values with indexes: 1..3",
               "[1] 2L => slice 0",
               "[2] 3L => slice 0",
               "[3] 4L => slice 0",
               )
         }
         "print errors unambiguously when the actual value is empty"  {
            shouldThrow<AssertionError> {
               emptyList<Long>() should endWith(listOf(1L, 3L))
            }.message.shouldContainInOrder(
               "List should end with [1L, 3L] but was []",
               "Actual collection is shorter than expected slice",
               "[0] 1L => Not Found",
               "[1] 3L => Not Found",
               )
         }
      }
   }
}
