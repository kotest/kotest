package com.sksamuel.kotest.matchers

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldStartWith

class ShouldNotBeNullTest : WordSpec() {
   init {
      "notBeNull" should {
         val nullString: String? = null
         val nonNullString: String? = "Foo"

         "Pass for a non-null value" {
            nonNullString.shouldNotBeNull()
            nonNullString shouldNot beNull()
         }

         "Fail for a null value" {
            shouldThrow<AssertionError> { nullString.shouldNotBeNull() }
            shouldThrow<AssertionError> { nullString shouldNot beNull() }
         }

         "Allow automatic type cast" {
            fun useString(string: String) {}

            nonNullString.shouldNotBeNull()
            useString(nonNullString)
            nonNullString shouldBe "Foo"
         }

         "return the receiver" {
            val a: String? = "foo"
            a.shouldNotBeNull().shouldHaveLength(3)

            val b: String? = null
            shouldFail { b.shouldNotBeNull() }
         }

         "allow fluent chaining" {
            val a: String? = "foo"
            a.shouldNotBeNull()
               .shouldHaveLength(3)
               .shouldStartWith("fo")
               .shouldEndWith("oo")
         }

         "accept a block" {
            val a: String? = "foo"

            a shouldNotBeNull {
               shouldHaveLength(3)
               shouldStartWith("fo")
               shouldEndWith("oo")
            }
         }
      }
   }
}
