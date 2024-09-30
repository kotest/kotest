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
            byteArrayOf() shouldHaveUpperBound 3
            byteArrayOf(3) shouldHaveUpperBound 3
            byteArrayOf(1, 2, 3) shouldHaveUpperBound 3
            byteArrayOf(1, 2, 3) shouldHaveUpperBound 4

            shortArrayOf() shouldHaveUpperBound 3
            shortArrayOf(3) shouldHaveUpperBound 3
            shortArrayOf(1, 2, 3) shouldHaveUpperBound 3
            shortArrayOf(1, 2, 3) shouldHaveUpperBound 4

            charArrayOf() shouldHaveUpperBound 'c'
            charArrayOf('c') shouldHaveUpperBound 'c'
            charArrayOf('a', 'b', 'c') shouldHaveUpperBound 'c'
            charArrayOf('a', 'b', 'c') shouldHaveUpperBound 'd'

            intArrayOf() shouldHaveUpperBound 3
            intArrayOf(3) shouldHaveUpperBound 3
            intArrayOf(1, 2, 3) shouldHaveUpperBound 3
            intArrayOf(1, 2, 3) shouldHaveUpperBound 4

            longArrayOf() shouldHaveUpperBound 3
            longArrayOf(3) shouldHaveUpperBound 3
            longArrayOf(1, 2, 3) shouldHaveUpperBound 3
            longArrayOf(1, 2, 3) shouldHaveUpperBound 4

            floatArrayOf() shouldHaveUpperBound 0.3f
            floatArrayOf(0.3f) shouldHaveUpperBound 0.3f
            floatArrayOf(0.1f, 0.2f, 0.3f) shouldHaveUpperBound 0.3f
            floatArrayOf(0.1f, 0.2f, 0.3f) shouldHaveUpperBound 4f

            doubleArrayOf() shouldHaveUpperBound 0.3
            doubleArrayOf(0.3) shouldHaveUpperBound 0.3
            doubleArrayOf(0.1, 0.2, 0.3) shouldHaveUpperBound 0.3
            doubleArrayOf(0.1, 0.2, 0.3) shouldHaveUpperBound 4.0

            arrayOf<Int>() shouldHaveUpperBound 3
            arrayOf(3) shouldHaveUpperBound 3
            arrayOf(1, 2, 3) shouldHaveUpperBound 3
            arrayOf(1, 2, 3) shouldHaveUpperBound 4

            listOf<Int>() shouldHaveUpperBound 3
            listOf(3) shouldHaveUpperBound 3
            listOf(1, 2, 3) shouldHaveUpperBound 3
            listOf(1, 2, 3) shouldHaveUpperBound 4

            1..0 shouldHaveUpperBound 3
            3..3 shouldHaveUpperBound 3
            1..3 shouldHaveUpperBound 3
            1..3 shouldHaveUpperBound 4
         }

         fun msg(name: String, bound: String = "2", violation: String = "3") =
            "$name should have upper bound $bound, but the following elements are above it: [$violation]"

         "fail for ByteArray" {
            shouldThrowAny { byteArrayOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("ByteArray"))
         }

         "fail for ShortArray" {
            shouldThrowAny { shortArrayOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("ShortArray"))
         }

         "fail for CharArray" {
            shouldThrowAny { charArrayOf('a', 'b', 'c') shouldHaveUpperBound 'b' }
               .shouldHaveMessage(msg("CharArray", "b", "'c'"))
         }

         "fail for IntArray" {
            shouldThrowAny { intArrayOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("IntArray"))
         }

         "fail for LongArray" {
            shouldThrowAny { longArrayOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("LongArray", "2", "3L"))
         }

         "fail for FloatArray" {
            shouldThrowAny { floatArrayOf(0.0f, 0.2f, 0.3f) shouldHaveUpperBound 0.2f }
               .shouldHaveMessage(msg("FloatArray", "0.2", "0.3f"))
         }

         "fail for DoubleArray" {
            shouldThrowAny { doubleArrayOf(0.0, 0.2, 0.3) shouldHaveUpperBound 0.2 }
               .shouldHaveMessage(msg("DoubleArray", "0.2", "0.3"))
         }

         "fail for Array" {
            shouldThrowAny { arrayOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("Array"))
         }

         "fail for List" {
            shouldThrowAny { listOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("List"))
         }

         "fail for Set" {
            shouldThrowAny { setOf(1, 2, 3) shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("Set"))
         }

         "fail for Range" {
            shouldThrowAny { 1..3 shouldHaveUpperBound 2 }
               .shouldHaveMessage(msg("Range"))
         }
      }

      "haveLowerBound" should {
         "pass" {
            byteArrayOf() shouldHaveLowerBound 3
            byteArrayOf(3) shouldHaveLowerBound 3
            byteArrayOf(1, 2, 3) shouldHaveLowerBound 1
            byteArrayOf(1, 2, 3) shouldHaveLowerBound 0

            shortArrayOf() shouldHaveLowerBound 3
            shortArrayOf(3) shouldHaveLowerBound 3
            shortArrayOf(1, 2, 3) shouldHaveLowerBound 1
            shortArrayOf(1, 2, 3) shouldHaveLowerBound 0

            charArrayOf() shouldHaveLowerBound 'c'
            charArrayOf('c') shouldHaveLowerBound 'c'
            charArrayOf('b', 'c', 'd') shouldHaveLowerBound 'b'
            charArrayOf('b', 'c', 'd') shouldHaveLowerBound 'a'

            intArrayOf() shouldHaveLowerBound 3
            intArrayOf(3) shouldHaveLowerBound 3
            intArrayOf(1, 2, 3) shouldHaveLowerBound 1
            intArrayOf(1, 2, 3) shouldHaveLowerBound 0

            longArrayOf() shouldHaveLowerBound 3
            longArrayOf(3) shouldHaveLowerBound 3
            longArrayOf(1, 2, 3) shouldHaveLowerBound 1
            longArrayOf(1, 2, 3) shouldHaveLowerBound 0

            floatArrayOf() shouldHaveLowerBound 0.3f
            floatArrayOf(0.3f) shouldHaveLowerBound 0.3f
            floatArrayOf(0.1f, 0.2f, 0.3f) shouldHaveLowerBound 0.1f
            floatArrayOf(0.1f, 0.2f, 0.3f) shouldHaveLowerBound 0f

            doubleArrayOf() shouldHaveLowerBound 0.3
            doubleArrayOf(0.3) shouldHaveLowerBound 0.3
            doubleArrayOf(0.1, 0.2, 0.3) shouldHaveLowerBound 0.1
            doubleArrayOf(0.1, 0.2, 0.3) shouldHaveLowerBound 0.0

            arrayOf<Int>() shouldHaveLowerBound 3
            arrayOf(3) shouldHaveLowerBound 3
            arrayOf(1, 2, 3) shouldHaveLowerBound 1
            arrayOf(1, 2, 3) shouldHaveLowerBound 0

            listOf<Int>() shouldHaveLowerBound 3
            listOf(3) shouldHaveLowerBound 3
            listOf(1, 2, 3) shouldHaveLowerBound 1
            listOf(1, 2, 3) shouldHaveLowerBound 0

            1..0 shouldHaveLowerBound 3
            3..3 shouldHaveLowerBound 3
            1..3 shouldHaveLowerBound 1
            1..3 shouldHaveLowerBound 0
         }

         fun msg(name: String, bound: String = "2", violation: String = "1") =
            "$name should have lower bound $bound, but the following elements are below it: [$violation]"

         "fail for ByteArray" {
            shouldThrowAny { byteArrayOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("ByteArray"))
         }

         "fail for ShortArray" {
            shouldThrowAny { shortArrayOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("ShortArray"))
         }

         "fail for CharArray" {
            shouldThrowAny { charArrayOf('a', 'b', 'c') shouldHaveLowerBound 'b' }
               .shouldHaveMessage(msg("CharArray", "b", "'a'"))
         }

         "fail for IntArray" {
            shouldThrowAny { intArrayOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("IntArray"))
         }

         "fail for LongArray" {
            shouldThrowAny { longArrayOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("LongArray", "2", "1L"))
         }

         "fail for FloatArray" {
            shouldThrowAny { floatArrayOf(0.1f, 0.2f, 0.3f) shouldHaveLowerBound 0.2f }
               .shouldHaveMessage(msg("FloatArray", "0.2", "0.1f"))
         }

         "fail for DoubleArray" {
            shouldThrowAny { doubleArrayOf(0.1, 0.2, 0.3) shouldHaveLowerBound 0.2 }
               .shouldHaveMessage(msg("DoubleArray", "0.2", "0.1"))
         }

         "fail for Array" {
            shouldThrowAny { arrayOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("Array"))
         }

         "fail for List" {
            shouldThrowAny { listOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("List"))
         }

         "fail for Set" {
            shouldThrowAny { setOf(1, 2, 3) shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("Set"))
         }

         "fail for Range" {
            shouldThrowAny { 1..3 shouldHaveLowerBound 2 }
               .shouldHaveMessage(msg("Range"))
         }
      }
   }
}
