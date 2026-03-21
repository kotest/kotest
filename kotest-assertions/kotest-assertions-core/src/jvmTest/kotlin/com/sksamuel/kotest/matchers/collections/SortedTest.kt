package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeSortedBy
import io.kotest.matchers.collections.shouldBeSortedDescending
import io.kotest.matchers.collections.shouldBeSortedDescendingBy
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldNotBeSorted
import io.kotest.matchers.collections.shouldNotBeSortedBy
import io.kotest.matchers.collections.shouldNotBeSortedWith
import io.kotest.matchers.collections.sorted
import io.kotest.matchers.collections.sortedDescending
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class SortedTest : WordSpec() {
   private val countdown = (10 downTo 0).toList()
   private val asc = { a: Int, b: Int -> a - b }
   private val desc = { a: Int, b: Int -> b - a }

   init {

      "a descending non-empty list" should {
         "fail to ascend" {
            shouldFail {
               countdown.shouldBeSortedWith(asc)
            }
         }

         "descend" {
            countdown.shouldBeSortedWith(desc)
         }

         "not ascend" {
            countdown.shouldNotBeSortedWith(asc)
         }

         "fail not to descend" {
            shouldFail {
               countdown.shouldNotBeSortedWith(desc)
            }
         }
      }

      "sortedWith" should {
         val items = listOf(
            1 to "I",
            2 to "II",
            4 to "IV",
            5 to "V",
            6 to "VI",
            9 to "IX",
            10 to "X"
         )

         "work on non-Comparable given a Comparator" {
            items.shouldBeSortedWith(Comparator { a, b -> asc(a.first, b.first) })
         }

         "work on non-Comparable given a compare function" {
            items.shouldBeSortedWith { a, b -> asc(a.first, b.first) }
         }
      }

      "sortedBy" should {
         val items = listOf(
            1 to "I",
            2 to "II",
            4 to "IV",
            5 to "V",
            6 to "VI",
            9 to "IX",
            10 to "X"
         )

         "compare by the tranformed value" {
            items.shouldBeSortedBy { it.first }
            items.shouldNotBeSortedBy { it.second }
         }

         "compare by the tranformed value in descending order" {
            items.shouldBeSortedDescendingBy { it.first * -1 }
         }
      }

      "sorted" should {
         "test that a collection is sorted" {
            emptyList<Int>() shouldBe sorted<Int>()
            listOf(1) shouldBe sorted<Int>()
            listOf(1, 2, 3, 4) shouldBe sorted<Int>()

            shouldThrow<AssertionError> {
               listOf(2, 1) shouldBe sorted<Int>()
            }.shouldHaveMessage("List [2, 1] should be sorted. Element 2 at index 0 was greater than element 1")

            listOf(1, 2, 6, 9).shouldBeSorted()

            shouldThrow<AssertionError> {
               listOf(2, 1).shouldBeSorted()
            }.shouldHaveMessage("List [2, 1] should be sorted. Element 2 at index 0 was greater than element 1")

            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotBeSorted()
            }.shouldHaveMessage("List [1, 2, 3] should not be sorted")
         }

         "test that a collection is sorted descending" {
            emptyList<Int>() shouldBe sortedDescending<Int>()
            listOf(1) shouldBe sortedDescending<Int>()
            listOf(4, 3, 2, 1) shouldBe sortedDescending<Int>()

            shouldThrow<AssertionError> {
               listOf(1, 2) shouldBe sortedDescending<Int>()
            }.shouldHaveMessage("List [1, 2] should be sorted. Element 1 at index 0 was less than element 2")

            listOf(9, 6, 2, 1).shouldBeSortedDescending()

            shouldThrow<AssertionError> {
               listOf(1, 2).shouldBeSortedDescending()
            }.shouldHaveMessage("List [1, 2] should be sorted. Element 1 at index 0 was less than element 2")
         }

         "restrict items at the error message" {
            val longList = (1..1000).toList()

            shouldThrow<AssertionError> {
               longList.shouldNotBeSorted()
            }.shouldHaveMessage("List [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, ...and 980 more (set 'kotest.assertions.collection.print.size' to see more / less items)] should not be sorted")
         }
      }

      "primitive arrays" should {

         "IntArray shouldBeSorted" {
            intArrayOf().shouldBeSorted()
            intArrayOf(1).shouldBeSorted()
            intArrayOf(1, 2, 3).shouldBeSorted()
            shouldThrow<AssertionError> { intArrayOf(3, 2, 1).shouldBeSorted() }
         }

         "IntArray shouldNotBeSorted" {
            intArrayOf(3, 2, 1).shouldNotBeSorted()
            shouldThrow<AssertionError> { intArrayOf(1, 2, 3).shouldNotBeSorted() }
         }

         "LongArray shouldBeSorted" {
            longArrayOf().shouldBeSorted()
            longArrayOf(1L).shouldBeSorted()
            longArrayOf(1L, 2L, 3L).shouldBeSorted()
            shouldThrow<AssertionError> { longArrayOf(3L, 2L, 1L).shouldBeSorted() }
         }

         "LongArray shouldNotBeSorted" {
            longArrayOf(3L, 2L, 1L).shouldNotBeSorted()
            shouldThrow<AssertionError> { longArrayOf(1L, 2L, 3L).shouldNotBeSorted() }
         }

         "DoubleArray shouldBeSorted" {
            doubleArrayOf().shouldBeSorted()
            doubleArrayOf(1.0).shouldBeSorted()
            doubleArrayOf(1.0, 2.0, 3.0).shouldBeSorted()
            shouldThrow<AssertionError> { doubleArrayOf(3.0, 2.0, 1.0).shouldBeSorted() }
         }

         "DoubleArray shouldNotBeSorted" {
            doubleArrayOf(3.0, 2.0, 1.0).shouldNotBeSorted()
            shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0).shouldNotBeSorted() }
         }

         "FloatArray shouldBeSorted" {
            floatArrayOf().shouldBeSorted()
            floatArrayOf(1.0f).shouldBeSorted()
            floatArrayOf(1.0f, 2.0f, 3.0f).shouldBeSorted()
            shouldThrow<AssertionError> { floatArrayOf(3.0f, 2.0f, 1.0f).shouldBeSorted() }
         }

         "FloatArray shouldNotBeSorted" {
            floatArrayOf(3.0f, 2.0f, 1.0f).shouldNotBeSorted()
            shouldThrow<AssertionError> { floatArrayOf(1.0f, 2.0f, 3.0f).shouldNotBeSorted() }
         }

         "ByteArray shouldBeSorted" {
            byteArrayOf().shouldBeSorted()
            byteArrayOf(1).shouldBeSorted()
            byteArrayOf(1, 2, 3).shouldBeSorted()
            shouldThrow<AssertionError> { byteArrayOf(3, 2, 1).shouldBeSorted() }
         }

         "ByteArray shouldNotBeSorted" {
            byteArrayOf(3, 2, 1).shouldNotBeSorted()
            shouldThrow<AssertionError> { byteArrayOf(1, 2, 3).shouldNotBeSorted() }
         }

         "ShortArray shouldBeSorted" {
            shortArrayOf().shouldBeSorted()
            shortArrayOf(1).shouldBeSorted()
            shortArrayOf(1, 2, 3).shouldBeSorted()
            shouldThrow<AssertionError> { shortArrayOf(3, 2, 1).shouldBeSorted() }
         }

         "ShortArray shouldNotBeSorted" {
            shortArrayOf(3, 2, 1).shouldNotBeSorted()
            shouldThrow<AssertionError> { shortArrayOf(1, 2, 3).shouldNotBeSorted() }
         }

         "CharArray shouldBeSorted" {
            charArrayOf().shouldBeSorted()
            charArrayOf('a').shouldBeSorted()
            charArrayOf('a', 'b', 'c').shouldBeSorted()
            shouldThrow<AssertionError> { charArrayOf('c', 'b', 'a').shouldBeSorted() }
         }

         "CharArray shouldNotBeSorted" {
            charArrayOf('c', 'b', 'a').shouldNotBeSorted()
            shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c').shouldNotBeSorted() }
         }

         "BooleanArray shouldBeSorted (false < true)" {
            booleanArrayOf().shouldBeSorted()
            booleanArrayOf(false).shouldBeSorted()
            booleanArrayOf(true).shouldBeSorted()
            booleanArrayOf(false, true).shouldBeSorted()
            booleanArrayOf(false, false).shouldBeSorted()
            booleanArrayOf(true, true).shouldBeSorted()
            shouldThrow<AssertionError> { booleanArrayOf(true, false).shouldBeSorted() }
         }

         "BooleanArray shouldNotBeSorted" {
            booleanArrayOf(true, false).shouldNotBeSorted()
            shouldThrow<AssertionError> { booleanArrayOf(false, true).shouldNotBeSorted() }
         }
      }
   }
}
