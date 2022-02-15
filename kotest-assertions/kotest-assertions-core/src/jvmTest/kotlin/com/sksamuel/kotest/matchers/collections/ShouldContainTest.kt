package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.equals.Equality
import io.kotest.equals.types.byObjectEquality
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainTest : WordSpec({
   "contain" should {
      "test that a collection contains element x"  {
         val col = listOf(1, 2, 3)
         shouldThrow<AssertionError> {
            col should contain(4)
         }
         shouldThrow<AssertionError> {
            col.shouldContain(4)
         }
         col should contain(2)
         col should contain(2.0)
      }

      "test that a collection contains element with a custom verifier"  {
         val col = listOf(1, 2, 3)
         val verifier = Equality.byObjectEquality<Number>(strictNumberEquality = true)

         shouldThrow<AssertionError> {
            col.shouldContain(2.0, verifier)
         }
         col should contain(2, verifier)
      }

      "support infix shouldContain" {
         val col = listOf(1, 2, 3)
         col shouldContain (2)
      }

      "support type inference for subtypes of collection" {
         val tests = listOf(
            TestSealed.Test1("test1"),
            TestSealed.Test2(2)
         )
         tests should contain(TestSealed.Test1("test1"))
         tests.shouldContain(TestSealed.Test2(2))
      }

      "print errors unambiguously"  {
         shouldThrow<AssertionError> {
            listOf<Any>(1, 2, 3, 4, 5, 6, 7).shouldContain(listOf<Any>(1L, 2L))
         }.shouldHaveMessage("Collection should contain element [1L, 2L] based on object equality; but the collection is [1, 2, 3, 4, 5, 6, 7]")
      }
   }
})
