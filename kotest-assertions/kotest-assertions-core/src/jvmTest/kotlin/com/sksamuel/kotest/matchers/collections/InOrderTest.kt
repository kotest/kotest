package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containsInOrder
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldNotContainInOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.throwable.shouldHaveMessage

class InOrderTest : WordSpec() {
   init {
      "containInOrder" should {
         "test that a collection contains the same elements in the given order, duplicates permitted" {
            val col = listOf(1, 1, 2, 2, 3, 3)

            col should containsInOrder(1, 2, 3)
            col should containsInOrder(1)

            shouldThrow<AssertionError> {
               col should containsInOrder(1, 2, 6)
            }.message shouldBe "[1, 1, 2, 2, 3, 3] did not contain the elements [1, 2, 6] in order, could not match element 6 at index 2"

            shouldThrow<AssertionError> {
               col should containsInOrder(4)
            }.message shouldBe "[1, 1, 2, 2, 3, 3] did not contain the elements [4] in order, could not match element 4 at index 0"

            shouldThrow<AssertionError> {
               col should containsInOrder(2, 1, 3)
            }
         }
         "reject empty expected list" {
            shouldThrowAny {
               listOf(5, 3, 1, 2, 4, 2) should containsInOrder(listOf<Int>())
            }.message shouldBe "expected values must not be empty"
         }
         "work with unsorted collections" {
            val actual = listOf(5, 3, 1, 2, 4, 2)
            actual should containsInOrder(3, 2, 2)
         }
         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf<Number>(1L, 2L) should containsInOrder(listOf<Number>(1, 2))
            }.shouldHaveMessage("""[1L, 2L] did not contain the elements [1, 2] in order, could not match element 1 at index 0""")
         }
         "support iterables with vararg" {
            val actual = listOf(1, 2, 3, 4, 5).asIterable()
            actual.shouldContainInOrder(2, 3, 4)
         }
         "support arrays with vararg" {
            val actual = arrayOf(1, 2, 3, 4, 5)
            actual.shouldContainInOrder(2, 3, 4)
         }
         "find mismatched element before" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3, 4, 5) should containsInOrder(3, 4, 5, 2)
            }.message.shouldContainInOrder(
               "did not contain the elements [3, 4, 5, 2] in order",
               "could not match element 2 at index 3",
               "but found it before at index(es) [1]"
            )
         }
      }

      "primitive array support" should {
         "support IntArray with vararg" {
            intArrayOf(1, 2, 3).shouldContainInOrder(1, 3)
            intArrayOf(1, 2, 3).shouldContainInOrder(1, 2, 3)
            shouldThrow<AssertionError> {
               intArrayOf(1, 2, 3).shouldContainInOrder(3, 1)
            }
         }
         "support IntArray infix with IntArray" {
            intArrayOf(1, 2, 3).shouldContainInOrder(intArrayOf(1, 3))
            shouldThrow<AssertionError> {
               intArrayOf(1, 2, 3).shouldContainInOrder(intArrayOf(3, 1))
            }
         }
         "support IntArray shouldNotContainInOrder with vararg" {
            intArrayOf(1, 2, 3).shouldNotContainInOrder(2, 4)
            shouldThrow<AssertionError> {
               intArrayOf(1, 2, 3).shouldNotContainInOrder(1, 3)
            }
         }
         "support IntArray shouldNotContainInOrder infix" {
            intArrayOf(1, 2, 3).shouldNotContainInOrder(intArrayOf(2, 4))
            shouldThrow<AssertionError> {
               intArrayOf(1, 2, 3).shouldNotContainInOrder(intArrayOf(1, 3))
            }
         }
         "support LongArray" {
            longArrayOf(1L, 2L, 3L).shouldContainInOrder(1L, 3L)
            longArrayOf(1L, 2L, 3L).shouldContainInOrder(longArrayOf(1L, 3L))
            longArrayOf(1L, 2L, 3L).shouldNotContainInOrder(2L, 4L)
            longArrayOf(1L, 2L, 3L).shouldNotContainInOrder(longArrayOf(2L, 4L))
         }
         "support DoubleArray" {
            doubleArrayOf(1.0, 2.0, 3.0).shouldContainInOrder(1.0, 3.0)
            doubleArrayOf(1.0, 2.0, 3.0).shouldContainInOrder(doubleArrayOf(1.0, 3.0))
            doubleArrayOf(1.0, 2.0, 3.0).shouldNotContainInOrder(2.0, 4.0)
            doubleArrayOf(1.0, 2.0, 3.0).shouldNotContainInOrder(doubleArrayOf(2.0, 4.0))
         }
         "support FloatArray" {
            floatArrayOf(1f, 2f, 3f).shouldContainInOrder(1f, 3f)
            floatArrayOf(1f, 2f, 3f).shouldContainInOrder(floatArrayOf(1f, 3f))
            floatArrayOf(1f, 2f, 3f).shouldNotContainInOrder(2f, 4f)
            floatArrayOf(1f, 2f, 3f).shouldNotContainInOrder(floatArrayOf(2f, 4f))
         }
         "support ByteArray" {
            byteArrayOf(1, 2, 3).shouldContainInOrder(1, 3)
            byteArrayOf(1, 2, 3).shouldContainInOrder(byteArrayOf(1, 3))
            byteArrayOf(1, 2, 3).shouldNotContainInOrder(2, 4)
            byteArrayOf(1, 2, 3).shouldNotContainInOrder(byteArrayOf(2, 4))
         }
         "support ShortArray" {
            shortArrayOf(1, 2, 3).shouldContainInOrder(1, 3)
            shortArrayOf(1, 2, 3).shouldContainInOrder(shortArrayOf(1, 3))
            shortArrayOf(1, 2, 3).shouldNotContainInOrder(2, 4)
            shortArrayOf(1, 2, 3).shouldNotContainInOrder(shortArrayOf(2, 4))
         }
         "support CharArray" {
            charArrayOf('a', 'b', 'c').shouldContainInOrder('a', 'c')
            charArrayOf('a', 'b', 'c').shouldContainInOrder(charArrayOf('a', 'c'))
            charArrayOf('a', 'b', 'c').shouldNotContainInOrder('b', 'd')
            charArrayOf('a', 'b', 'c').shouldNotContainInOrder(charArrayOf('b', 'd'))
         }
         "support BooleanArray" {
            booleanArrayOf(true, false, true).shouldContainInOrder(true, true)
            booleanArrayOf(true, false, true).shouldContainInOrder(booleanArrayOf(true, true))
            booleanArrayOf(true, false, true).shouldNotContainInOrder(false, false)
            booleanArrayOf(true, false, true).shouldNotContainInOrder(booleanArrayOf(false, false))
         }
      }
   }
}
