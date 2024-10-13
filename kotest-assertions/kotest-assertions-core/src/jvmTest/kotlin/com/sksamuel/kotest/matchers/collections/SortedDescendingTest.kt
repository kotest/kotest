package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.beSortedDescending
import io.kotest.matchers.collections.beSortedDescendingBy
import io.kotest.matchers.collections.shouldBeSortedDescending
import io.kotest.matchers.collections.shouldBeSortedDescendingBy
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class SortedDescendingTest : WordSpec() {
   init {
      "shouldBeSortedDescending" should {
         "succeed for ByteArray" {
            byteArrayOf().shouldBeSortedDescending()
            byteArrayOf(1).shouldBeSortedDescending()
            byteArrayOf(2, 2).shouldBeSortedDescending()
            byteArrayOf(2, 1).shouldBeSortedDescending()
         }
         "succeed for ShortArray" {
            shortArrayOf().shouldBeSortedDescending()
            shortArrayOf(1).shouldBeSortedDescending()
            shortArrayOf(2, 2).shouldBeSortedDescending()
            shortArrayOf(2, 1).shouldBeSortedDescending()
         }
         "succeed for CharArray" {
            charArrayOf().shouldBeSortedDescending()
            charArrayOf('1').shouldBeSortedDescending()
            charArrayOf('2', '2').shouldBeSortedDescending()
            charArrayOf('2', '1').shouldBeSortedDescending()
         }
         "succeed for IntArray" {
            intArrayOf().shouldBeSortedDescending()
            intArrayOf(1).shouldBeSortedDescending()
            intArrayOf(2, 2).shouldBeSortedDescending()
            intArrayOf(2, 1).shouldBeSortedDescending()
         }
         "succeed for LongArray" {
            longArrayOf().shouldBeSortedDescending()
            longArrayOf(1).shouldBeSortedDescending()
            longArrayOf(2, 2).shouldBeSortedDescending()
            longArrayOf(2, 1).shouldBeSortedDescending()
         }
         "succeed for FloatArray" {
            floatArrayOf().shouldBeSortedDescending()
            floatArrayOf(1f).shouldBeSortedDescending()
            floatArrayOf(2f, 2f).shouldBeSortedDescending()
            floatArrayOf(2f, 1f).shouldBeSortedDescending()
         }
         "succeed for DoubleArray" {
            doubleArrayOf().shouldBeSortedDescending()
            doubleArrayOf(1.0).shouldBeSortedDescending()
            doubleArrayOf(2.0, 2.0).shouldBeSortedDescending()
            doubleArrayOf(2.0, 1.0).shouldBeSortedDescending()
         }
         "succeed for typed Array" {
            arrayOf<Int>().shouldBeSortedDescending()
            arrayOf(1).shouldBeSortedDescending()
            arrayOf(2, 2).shouldBeSortedDescending()
            arrayOf(2, 1).shouldBeSortedDescending()
         }
         "succeed for List" {
            listOf<Int>().shouldBeSortedDescending()
            listOf(1).shouldBeSortedDescending()
            listOf(2, 2).shouldBeSortedDescending()
            listOf(2, 1).shouldBeSortedDescending()

            listOf<Int>() should beSortedDescending()
            listOf(1) should beSortedDescending()
            listOf(2, 2) should beSortedDescending()
            listOf(2, 1) should beSortedDescending()
         }
         "succeed for Range" {
            (1..0).shouldBeSortedDescending()
            (1..1).shouldBeSortedDescending()
            (2 downTo 3).shouldBeSortedDescending()
            (2 downTo 2).shouldBeSortedDescending()
            (2 downTo 1).shouldBeSortedDescending()

            (1..0) should beSortedDescending()
            (1..1) should beSortedDescending()
            (2 downTo 3) should beSortedDescending()
            (2 downTo 2) should beSortedDescending()
            (2 downTo 1) should beSortedDescending()
         }

         "fail for ByteArray" {
            shouldThrowAny { byteArrayOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "ByteArray should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for ShortArray" {
            shouldThrowAny { shortArrayOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "ShortArray should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for CharArray" {
            shouldThrowAny { charArrayOf('1', '2').shouldBeSortedDescending() }
               .message shouldBe "CharArray should be sorted in descending order. Element '1' at index 0 was smaller than element '2' at index 1"
         }
         "fail for IntArray" {
            shouldThrowAny { intArrayOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "IntArray should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for LongArray" {
            shouldThrowAny { longArrayOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "LongArray should be sorted in descending order. Element 1L at index 0 was smaller than element 2L at index 1"
         }
         "fail for FloatArray" {
            shouldThrowAny { floatArrayOf(1f, 2f).shouldBeSortedDescending() }
               .message shouldBe "FloatArray should be sorted in descending order. Element 1.0f at index 0 was smaller than element 2.0f at index 1"
         }
         "fail for DoubleArray" {
            shouldThrowAny { doubleArrayOf(1.0, 2.0).shouldBeSortedDescending() }
               .message shouldBe "DoubleArray should be sorted in descending order. Element 1.0 at index 0 was smaller than element 2.0 at index 1"
         }
         "fail for typed Array" {
            shouldThrowAny { arrayOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "Array should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for List" {
            shouldThrowAny { listOf(1, 2).shouldBeSortedDescending() }
               .message shouldBe "List should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for Range" {
            shouldThrowAny { (1..2).shouldBeSortedDescending() }
               .message shouldBe "Range should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
      }

      "shouldBeSortedDescendingBy" should {
         "succeed for typed Array" {
            arrayOf<Int>() shouldBeSortedDescendingBy { it }
            arrayOf(1).shouldBeSortedDescendingBy { it }
            arrayOf(2, 2).shouldBeSortedDescendingBy { it }
            arrayOf(2, 1).shouldBeSortedDescendingBy { it }
         }

         "succeed for List" {
            listOf<Int>() shouldBeSortedDescendingBy { it }
            listOf(1).shouldBeSortedDescendingBy { it }
            listOf(2, 2).shouldBeSortedDescendingBy { it }
            listOf(2, 1).shouldBeSortedDescendingBy { it }

            listOf<Int>() should beSortedDescendingBy { it }
            listOf(1) should beSortedDescendingBy { it }
            listOf(2, 2) should beSortedDescendingBy { it }
            listOf(2, 1) should beSortedDescendingBy { it }
         }

         "fail for typed Array" {
            shouldThrowAny { arrayOf(1, 2).shouldBeSortedDescendingBy { it } }
               .message shouldBe "Array should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }

         "fail for List" {
            shouldThrowAny { listOf(1, 2).shouldBeSortedDescendingBy { it } }
               .message shouldBe "List should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
         "fail for Range" {
            shouldThrowAny { (1..2).shouldBeSortedDescendingBy { it } }
               .message shouldBe "Range should be sorted in descending order. Element 1 at index 0 was smaller than element 2 at index 1"
         }
      }
   }

}
