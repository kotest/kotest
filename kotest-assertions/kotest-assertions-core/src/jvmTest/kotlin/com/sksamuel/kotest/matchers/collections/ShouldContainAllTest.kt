package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.should
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
         "print missing elements" {
            shouldThrow<AssertionError> {
               listOf<Number>(1, 2).shouldContainAll(listOf<Number>(1L, 2L))
            }.shouldHaveMessage("""Collection should contain all of [1L, 2L] based on object equality but was missing [1L, 2L]""")
         }
      }
   }
}
