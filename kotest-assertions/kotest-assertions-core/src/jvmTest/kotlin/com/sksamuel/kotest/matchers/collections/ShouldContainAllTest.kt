package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldEndWith
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
               |Collection should contain all of [Fruit(name=apple, color=green, taste=sweet), Fruit(name=apple, color=red, taste=sweet)] but was missing [Fruit(name=apple, color=red, taste=sweet)]Possible matches:
               | expected: Fruit(name=apple, color=red, taste=sweet),
               |  but was: Fruit(name=apple, color=green, taste=sweet),
               |  The following fields did not match:
               |    "color" expected: <"red">, but was: <"green">
               |
               | expected: Fruit(name=apple, color=red, taste=sweet),
               |  but was: Fruit(name=pear, color=green, taste=sweet),
               |  The following fields did not match:
               |    "name" expected: <"apple">, but was: <"pear">
               |        "color" expected: <"red">, but was: <"green">
    """.trimMargin())
         }

         "print two possible matches for one mismatched element" {
            shouldThrowAny {
               listOf(sweetRedApple, sweetGreenPear, sourYellowLemon).shouldContainAll(
                  listOf(sweetGreenApple, sourYellowLemon)
               )
            }.message.shouldEndWith("""
               | expected: Fruit(name=apple, color=green, taste=sweet),
               |  but was: Fruit(name=apple, color=red, taste=sweet),
               |  The following fields did not match:
               |    "color" expected: <"green">, but was: <"red">
               |
               | expected: Fruit(name=apple, color=green, taste=sweet),
               |  but was: Fruit(name=pear, color=green, taste=sweet),
               |  The following fields did not match:
               |    "name" expected: <"apple">, but was: <"pear">
    """.trimMargin())
         }
      }
   }
}
