package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactCopies
import io.kotest.matchers.collections.shouldNotContainExactCopies
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder

class ContainExactCopiesTest : WordSpec() {
   init {
      "shouldContainExactCopies" should {
         "pass if contains exact count" {
            listOf(1, 2, 3).shouldContainExactCopies(t = 2, copies = 1)
            listOf(3, 1, 2, 3).shouldContainExactCopies(t = 3, copies = 2)
            arrayOf(1, 2, 3).shouldContainExactCopies(t = 2, copies = 1)
            arrayOf(3, 1, 2, 3).shouldContainExactCopies(t = 3, copies = 2)
            intArrayOf(1, 2, 3).shouldContainExactCopies(t = 2, copies = 1)
            longArrayOf(1L, 2L, 3L).shouldContainExactCopies(t = 2L, copies = 1)
            charArrayOf('1', '2', '3').shouldContainExactCopies(t = '2', copies = 1)
            byteArrayOf(1, 2, 3).shouldContainExactCopies(t = 2, copies = 1)
            shortArrayOf(1, 2, 3).shouldContainExactCopies(t = 2, copies = 1)
            floatArrayOf(1f, 2f, 3f).shouldContainExactCopies(t = 2f, copies = 1)
            doubleArrayOf(1.0, 2.0, 3.0).shouldContainExactCopies(t = 2.0, copies = 1)
         }
         "fail if the count does not match" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3, 3).shouldContainExactCopies(t = 3, copies = 1)
               arrayOf(1, 2, 3, 3).shouldContainExactCopies(t = 3, copies = 1)
            }.message.shouldContainInOrder(
               "Collection should contain 1 copies of element 3",
               "but contained 2 copies at index(es) [2, 3]"
            )
         }
      }
      "shouldNotContainExactCopies" should {
         "fail if the count matches" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3, 3).shouldNotContainExactCopies(t = 3, copies = 2)
               arrayOf(1, 2, 3, 3).shouldNotContainExactCopies(t = 3, copies = 2)
            }.message.shouldContain(
               "Collection should not contain 2 copies of element 3, but it did at index(es):[2, 3]"
            )
         }
         "pass if the count does not match" {
            listOf(1, 2, 3, 3).shouldNotContainExactCopies(t = 3, copies = 1)
            arrayOf(1, 2, 3, 3).shouldNotContainExactCopies(t = 3, copies = 1)
            intArrayOf(1, 2, 3).shouldNotContainExactCopies(t = 2, copies = 2)
            longArrayOf(1L, 2L, 3L).shouldNotContainExactCopies(t = 2L, copies = 2)
            charArrayOf('1', '2', '3').shouldNotContainExactCopies(t = '2', copies = 2)
            byteArrayOf(1, 2, 3).shouldNotContainExactCopies(t = 2, copies = 2)
            shortArrayOf(1, 2, 3).shouldNotContainExactCopies(t = 2, copies = 2)
            floatArrayOf(1f, 2f, 3f).shouldNotContainExactCopies(t = 2f, copies = 2)
            doubleArrayOf(1.0, 2.0, 3.0).shouldNotContainExactCopies(t = 2.0, copies = 2)
         }
      }
   }
}
