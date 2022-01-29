package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.equals.EqualityVerifiers
import io.kotest.equals.types.objectEquality
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
         val verifier = EqualityVerifiers.objectEquality<Number>(strictNumberEquality = true)

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
            listOf<Any>(1, 2).shouldContain(listOf<Any>(1L, 2L))
         }.shouldHaveMessage("Collection should contain element [1L, 2L] based on object equality; listing some elements [1, 2]")
      }
   }
})
