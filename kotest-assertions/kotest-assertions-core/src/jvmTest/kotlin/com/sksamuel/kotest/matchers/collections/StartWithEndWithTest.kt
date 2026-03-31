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
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldStartWith

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
         "find an element not matched as part of slice elsewhere" {
            val message = shouldThrow<AssertionError> {
               listOf(0L, 1L, 2L, 3L, 4L, 5L, 6L) should startWith(listOf(6L, 1L, 2L, 3L, 4L))
            }.message
            message.shouldContainInOrder(
               "Slice[0] of expected with indexes: 1..4 matched a slice of actual values with indexes: 1..4",
               "[1] 1L => slice 0",
               "[2] 2L => slice 0",
               "[3] 3L => slice 0",
               "[4] 4L => slice 0",
               "Element(s) not in matched slice(s):",
               "[0] 6L => Found At Index(es): [6]",
            )
         }
         "for an element not matched as part of slice, find a similar one" {
            val message = shouldThrow<AssertionError> {
               listOf(sweetGreenApple, sweetRedCherry, sweetYellowPear, bitterPurplePlum) shouldStartWith
                  listOf(sweetGreenApple, sweetRedCherry, sweetGreenPear)
            }.message
            message.shouldContainInOrder(
               "Possible matches:",
               "expected: Fruit(name=pear, color=green, taste=sweet),",
               "but was: Fruit(name=apple, color=green, taste=sweet),",
               "The following fields did not match:",
               """"name" expected: <"pear">, but was: <"apple">""",
               "expected: Fruit(name=pear, color=green, taste=sweet),",
               "but was: Fruit(name=pear, color=yellow, taste=sweet),",
               "The following fields did not match:",
               """"color" expected: <"green">, but was: <"yellow">""",
            )
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
               )
         }
         "find an element not matched as part of slice elsewhere" {
            val message = shouldThrow<AssertionError> {
               listOf(0L, 1L, 2L, 3L, 4L, 5L, 6L) should endWith(listOf(6L, 2L, 3L, 4L, 5L))
            }.message
            message.shouldContainInOrder(
               "Slice[0] of expected with indexes: 1..4 matched a slice of actual values with indexes: 2..5",
               "[2] 2L => slice 0",
               "[3] 3L => slice 0",
               "[4] 4L => slice 0",
               "[5] 5L => slice 0",
               "Element(s) not in matched slice(s):",
               "[0] 6L => Found At Index(es): [6]",
            )
         }
         "for an element not matched as part of slice, find a similar one" {
            val message = shouldThrow<AssertionError> {
               listOf(bitterPurplePlum, sweetGreenApple, sweetRedCherry, sweetYellowPear) shouldEndWith
                  listOf(sweetGreenApple, sweetRedCherry, sweetGreenPear)
            }.message
            message.shouldContainInOrder(
               "Possible matches:",
               "expected: Fruit(name=pear, color=green, taste=sweet),",
               "but was: Fruit(name=apple, color=green, taste=sweet),",
               "The following fields did not match:",
               """"name" expected: <"pear">, but was: <"apple">""",
               "expected: Fruit(name=pear, color=green, taste=sweet),",
               "but was: Fruit(name=pear, color=yellow, taste=sweet),",
               "The following fields did not match:",
               """"color" expected: <"green">, but was: <"yellow">""",
            )
         }

      }

      "primitive array startWith/endWith" should {
         "BooleanArray shouldStartWith and shouldEndWith" {
            booleanArrayOf(true, false, true, false).shouldStartWith(booleanArrayOf(true, false))
            booleanArrayOf(true, false, true, false).shouldEndWith(booleanArrayOf(true, false))
            booleanArrayOf(true, false, true, false).shouldNotStartWith(booleanArrayOf(false, true))
            booleanArrayOf(true, false, true, false).shouldNotEndWith(booleanArrayOf(true, true))
            shouldThrow<AssertionError> { booleanArrayOf(true, false).shouldStartWith(booleanArrayOf(false, true)) }
            shouldThrow<AssertionError> { booleanArrayOf(true, false).shouldEndWith(booleanArrayOf(false, false)) }
         }
         "ByteArray shouldStartWith and shouldEndWith" {
            byteArrayOf(1, 2, 3, 4).shouldStartWith(byteArrayOf(1, 2))
            byteArrayOf(1, 2, 3, 4).shouldEndWith(byteArrayOf(3, 4))
            byteArrayOf(1, 2, 3, 4).shouldNotStartWith(byteArrayOf(2, 3))
            byteArrayOf(1, 2, 3, 4).shouldNotEndWith(byteArrayOf(1, 2))
            shouldThrow<AssertionError> { byteArrayOf(1, 2, 3, 4).shouldStartWith(byteArrayOf(2, 3)) }
            shouldThrow<AssertionError> { byteArrayOf(1, 2, 3, 4).shouldEndWith(byteArrayOf(1, 2)) }
         }
         "ShortArray shouldStartWith and shouldEndWith" {
            shortArrayOf(1, 2, 3, 4).shouldStartWith(shortArrayOf(1, 2))
            shortArrayOf(1, 2, 3, 4).shouldEndWith(shortArrayOf(3, 4))
            shortArrayOf(1, 2, 3, 4).shouldNotStartWith(shortArrayOf(2, 3))
            shortArrayOf(1, 2, 3, 4).shouldNotEndWith(shortArrayOf(1, 2))
            shouldThrow<AssertionError> { shortArrayOf(1, 2, 3, 4).shouldStartWith(shortArrayOf(2, 3)) }
            shouldThrow<AssertionError> { shortArrayOf(1, 2, 3, 4).shouldEndWith(shortArrayOf(1, 2)) }
         }
         "CharArray shouldStartWith and shouldEndWith" {
            charArrayOf('a', 'b', 'c', 'd').shouldStartWith(charArrayOf('a', 'b'))
            charArrayOf('a', 'b', 'c', 'd').shouldEndWith(charArrayOf('c', 'd'))
            charArrayOf('a', 'b', 'c', 'd').shouldNotStartWith(charArrayOf('b', 'c'))
            charArrayOf('a', 'b', 'c', 'd').shouldNotEndWith(charArrayOf('a', 'b'))
            shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c', 'd').shouldStartWith(charArrayOf('b', 'c')) }
            shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c', 'd').shouldEndWith(charArrayOf('a', 'b')) }
         }
         "IntArray shouldStartWith and shouldEndWith" {
            intArrayOf(1, 2, 3, 4).shouldStartWith(intArrayOf(1, 2))
            intArrayOf(1, 2, 3, 4).shouldEndWith(intArrayOf(3, 4))
            intArrayOf(1, 2, 3, 4).shouldNotStartWith(intArrayOf(2, 3))
            intArrayOf(1, 2, 3, 4).shouldNotEndWith(intArrayOf(1, 2))
            shouldThrow<AssertionError> { intArrayOf(1, 2, 3, 4).shouldStartWith(intArrayOf(2, 3)) }
            shouldThrow<AssertionError> { intArrayOf(1, 2, 3, 4).shouldEndWith(intArrayOf(1, 2)) }
         }
         "LongArray shouldStartWith and shouldEndWith" {
            longArrayOf(1, 2, 3, 4).shouldStartWith(longArrayOf(1, 2))
            longArrayOf(1, 2, 3, 4).shouldEndWith(longArrayOf(3, 4))
            longArrayOf(1, 2, 3, 4).shouldNotStartWith(longArrayOf(2, 3))
            longArrayOf(1, 2, 3, 4).shouldNotEndWith(longArrayOf(1, 2))
            shouldThrow<AssertionError> { longArrayOf(1, 2, 3, 4).shouldStartWith(longArrayOf(2, 3)) }
            shouldThrow<AssertionError> { longArrayOf(1, 2, 3, 4).shouldEndWith(longArrayOf(1, 2)) }
         }
         "FloatArray shouldStartWith and shouldEndWith" {
            floatArrayOf(1f, 2f, 3f, 4f).shouldStartWith(floatArrayOf(1f, 2f))
            floatArrayOf(1f, 2f, 3f, 4f).shouldEndWith(floatArrayOf(3f, 4f))
            floatArrayOf(1f, 2f, 3f, 4f).shouldNotStartWith(floatArrayOf(2f, 3f))
            floatArrayOf(1f, 2f, 3f, 4f).shouldNotEndWith(floatArrayOf(1f, 2f))
            shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f, 4f).shouldStartWith(floatArrayOf(2f, 3f)) }
            shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f, 4f).shouldEndWith(floatArrayOf(1f, 2f)) }
         }
         "DoubleArray shouldStartWith and shouldEndWith" {
            doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldStartWith(doubleArrayOf(1.0, 2.0))
            doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldEndWith(doubleArrayOf(3.0, 4.0))
            doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldNotStartWith(doubleArrayOf(2.0, 3.0))
            doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldNotEndWith(doubleArrayOf(1.0, 2.0))
            shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldStartWith(doubleArrayOf(2.0, 3.0)) }
            shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0, 4.0).shouldEndWith(doubleArrayOf(1.0, 2.0)) }
         }
      }
   }
}
