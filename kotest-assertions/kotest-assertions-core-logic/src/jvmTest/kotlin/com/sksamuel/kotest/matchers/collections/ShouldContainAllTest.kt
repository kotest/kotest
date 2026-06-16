package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainAllTest : WordSpec() {

   init {

      "containsAll" should {
         "test that a collection contains all the elements but in any order" {
            val col = listOf(1, 2, 3, 4, 5)

            col should containAll(1, 2, 3)
            col should containAll(3, 2, 1)
            col should containAll(5, 1)
            col should containAll(1, 5)
            col should containAll(1)
            col should containAll(5)

            col.shouldContainAll(1, 2, 3)
            col.shouldContainAll(3, 1)
            col.shouldContainAll(3)

            col.shouldNotContainAll(6)
            col.shouldNotContainAll(1, 6)
            col.shouldNotContainAll(6, 1)

            shouldThrow<AssertionError> {
               col should containAll(1, 2, 6)
            }

            shouldThrow<AssertionError> {
               col should containAll(6)
            }

            shouldThrow<AssertionError> {
               col should containAll(0, 1, 2)
            }

            shouldThrow<AssertionError> {
               col should containAll(3, 2, 0)
            }
         }

         "test that a iterable contains all the elements but in any order" {
            val iter = listOf(1, 2, 3, 4, 5).asIterable()

            iter.shouldContainAll(1, 2, 3)
            iter.shouldContainAll(3, 1)
            iter.shouldContainAll(3)

            iter.shouldNotContainAll(6)
            iter.shouldNotContainAll(1, 6)
            iter.shouldNotContainAll(6, 1)

            shouldThrow<AssertionError> {
               iter.shouldContainAll(1, 2, 6)
            }

            shouldThrow<AssertionError> {
               iter.shouldContainAll(6)
            }

            shouldThrow<AssertionError> {
               iter.shouldContainAll(0, 1, 2)
            }

            shouldThrow<AssertionError> {
               iter.shouldContainAll(3, 2, 0)
            }

            shouldThrow<AssertionError> {
               iter.shouldNotContainAll(1, 2)
            }
         }

         "test that a array contains all the elements but in any order" {
            val arr = arrayOf(1, 2, 3, 4, 5)

            arr.shouldContainAll(1, 2, 3)
            arr.shouldContainAll(3, 1)
            arr.shouldContainAll(3)

            arr.shouldNotContainAll(6)
            arr.shouldNotContainAll(1, 6)
            arr.shouldNotContainAll(6, 1)

            shouldThrow<AssertionError> {
               arr.shouldContainAll(1, 2, 6)
            }

            shouldThrow<AssertionError> {
               arr.shouldContainAll(6)
            }

            shouldThrow<AssertionError> {
               arr.shouldContainAll(0, 1, 2)
            }

            shouldThrow<AssertionError> {
               arr.shouldContainAll(3, 2, 0)
            }

            shouldThrow<AssertionError> {
               arr.shouldNotContainAll(1, 2)
            }
         }

         "print missing elements" {
            shouldThrow<AssertionError> {
               listOf<Number>(1, 2).shouldContainAll(listOf<Number>(1L, 2L))
            }.shouldHaveMessage("""Collection should contain all of [1L, 2L] but was missing [1L, 2L]""")
         }

         "print one possible match for one mismatched element" {
            shouldThrowAny {
               listOf(sweetGreenApple, sweetGreenPear, sourYellowLemon).shouldContainAll(
                  listOf(sweetGreenApple, sweetRedApple)
               )
            }.shouldHaveMessage("""
               |Collection should contain all of [Fruit(name=apple, color=green, taste=sweet), Fruit(name=apple, color=red, taste=sweet)] but was missing [Fruit(name=apple, color=red, taste=sweet)]
               |Possible matches: expected: Fruit(name=apple, color=red, taste=sweet),
               |  but was: Fruit(name=apple, color=green, taste=sweet),
               |  The following fields did not match:
               |    "color" expected: <"red">, but was: <"green">
    """.trimMargin())
         }

         "print two possible matches for one mismatched element" {
            val message = shouldThrowAny {
               listOf(sweetRedApple, sweetGreenPear, sourYellowLemon).shouldContainAll(
                  listOf(sweetGreenApple, sourYellowLemon)
               )
            }.message
            message.shouldContain("""
               | expected: Fruit(name=apple, color=green, taste=sweet),
               |  but was: Fruit(name=apple, color=red, taste=sweet),
               |  The following fields did not match:
               |    "color" expected: <"green">, but was: <"red">
    """.trimMargin())
            message.shouldContain("""
               | expected: Fruit(name=apple, color=green, taste=sweet),
               |  but was: Fruit(name=pear, color=green, taste=sweet),
               |  The following fields did not match:
               |    "name" expected: <"apple">, but was: <"pear">
    """.trimMargin())
         }

         "test that BooleanArray supports shouldContainAll" {
            booleanArrayOf(true, false, true).shouldContainAll(true, false)
            booleanArrayOf(true, false, true).shouldContainAll(booleanArrayOf(true, false))
            booleanArrayOf(true).shouldNotContainAll(false)
            booleanArrayOf(true).shouldNotContainAll(booleanArrayOf(false))
            shouldThrow<AssertionError> { booleanArrayOf(true).shouldContainAll(false) }
            shouldThrow<AssertionError> { booleanArrayOf(true).shouldContainAll(booleanArrayOf(false)) }
         }

         "test that ByteArray supports shouldContainAll" {
            byteArrayOf(1, 2, 3).shouldContainAll(1.toByte(), 2.toByte())
            byteArrayOf(1, 2, 3).shouldContainAll(byteArrayOf(1, 2))
            byteArrayOf(1, 2, 3).shouldNotContainAll(6.toByte())
            byteArrayOf(1, 2, 3).shouldNotContainAll(byteArrayOf(6))
            shouldThrow<AssertionError> { byteArrayOf(1, 2, 3).shouldContainAll(6.toByte()) }
            shouldThrow<AssertionError> { byteArrayOf(1, 2, 3).shouldContainAll(byteArrayOf(6)) }
         }

         "test that ShortArray supports shouldContainAll" {
            shortArrayOf(1, 2, 3).shouldContainAll(1.toShort(), 2.toShort())
            shortArrayOf(1, 2, 3).shouldContainAll(shortArrayOf(1, 2))
            shortArrayOf(1, 2, 3).shouldNotContainAll(6.toShort())
            shortArrayOf(1, 2, 3).shouldNotContainAll(shortArrayOf(6))
            shouldThrow<AssertionError> { shortArrayOf(1, 2, 3).shouldContainAll(6.toShort()) }
            shouldThrow<AssertionError> { shortArrayOf(1, 2, 3).shouldContainAll(shortArrayOf(6)) }
         }

         "test that CharArray supports shouldContainAll" {
            charArrayOf('a', 'b', 'c').shouldContainAll('a', 'b')
            charArrayOf('a', 'b', 'c').shouldContainAll(charArrayOf('a', 'b'))
            charArrayOf('a', 'b', 'c').shouldNotContainAll('z')
            charArrayOf('a', 'b', 'c').shouldNotContainAll(charArrayOf('z'))
            shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c').shouldContainAll('z') }
            shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c').shouldContainAll(charArrayOf('z')) }
         }

         "test that IntArray supports shouldContainAll" {
            intArrayOf(1, 2, 3).shouldContainAll(1, 2)
            intArrayOf(1, 2, 3).shouldContainAll(intArrayOf(1, 2))
            intArrayOf(1, 2, 3).shouldNotContainAll(6)
            intArrayOf(1, 2, 3).shouldNotContainAll(intArrayOf(6))
            shouldThrow<AssertionError> { intArrayOf(1, 2, 3).shouldContainAll(6) }
            shouldThrow<AssertionError> { intArrayOf(1, 2, 3).shouldContainAll(intArrayOf(6)) }
         }

         "test that LongArray supports shouldContainAll" {
            longArrayOf(1L, 2L, 3L).shouldContainAll(1L, 2L)
            longArrayOf(1L, 2L, 3L).shouldContainAll(longArrayOf(1L, 2L))
            longArrayOf(1L, 2L, 3L).shouldNotContainAll(6L)
            longArrayOf(1L, 2L, 3L).shouldNotContainAll(longArrayOf(6L))
            shouldThrow<AssertionError> { longArrayOf(1L, 2L, 3L).shouldContainAll(6L) }
            shouldThrow<AssertionError> { longArrayOf(1L, 2L, 3L).shouldContainAll(longArrayOf(6L)) }
         }

         "test that FloatArray supports shouldContainAll" {
            floatArrayOf(1f, 2f, 3f).shouldContainAll(1f, 2f)
            floatArrayOf(1f, 2f, 3f).shouldContainAll(floatArrayOf(1f, 2f))
            floatArrayOf(1f, 2f, 3f).shouldNotContainAll(6f)
            floatArrayOf(1f, 2f, 3f).shouldNotContainAll(floatArrayOf(6f))
            shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f).shouldContainAll(6f) }
            shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f).shouldContainAll(floatArrayOf(6f)) }
         }

         "test that DoubleArray supports shouldContainAll" {
            doubleArrayOf(1.0, 2.0, 3.0).shouldContainAll(1.0, 2.0)
            doubleArrayOf(1.0, 2.0, 3.0).shouldContainAll(doubleArrayOf(1.0, 2.0))
            doubleArrayOf(1.0, 2.0, 3.0).shouldNotContainAll(6.0)
            doubleArrayOf(1.0, 2.0, 3.0).shouldNotContainAll(doubleArrayOf(6.0))
            shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0).shouldContainAll(6.0) }
            shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0).shouldContainAll(doubleArrayOf(6.0)) }
         }
      }
   }
}
