package com.sksamuel.kotest.matchers.boolean

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldNotBeFalse
import io.kotest.matchers.booleans.shouldNotBeTrue
import io.kotest.matchers.shouldBe

@Suppress("SimplifyBooleanWithConstants")
class BooleanMatchersTest : FreeSpec() {

   init {
      "Boolean shouldBeTrue should not fail for true booleans" {
         true.shouldBeTrue()
         (3 + 3 == 6).shouldBeTrue()
      }

      "Boolean shouldBeTrue should fail for false booleans" {
         shouldThrow<AssertionError> { false.shouldBeTrue() }
      }

      "Boolean shouldBeTrue should fail for null" {
         shouldThrow<AssertionError> { null.shouldBeTrue() }
      }

      "Boolean shouldBeFalse should not fail for false booleans" {
         false.shouldBeFalse()
         (3 + 3 == 42).shouldBeFalse()
      }

      "Boolean shouldBeFalse should fail for true booleans" {
         shouldThrow<AssertionError> { true.shouldBeFalse() }
      }

      "Boolean shouldBeFalse should fail for null" {
         shouldThrow<AssertionError> { null.shouldBeFalse() }
      }

      "Boolean shouldNotBeFalse should not fail for true booleans" {
         true.shouldNotBeFalse()
         (3 + 3 == 6).shouldNotBeFalse()
      }

      "Boolean shouldNotBeFalse should fail for false booleans" {
         shouldThrow<AssertionError> {
            false.shouldNotBeFalse()
         }.message shouldBe "false should not equal false"

         shouldThrow<AssertionError> {
            (3 + 3 == 7).shouldNotBeFalse()
         }.message shouldBe "false should not equal false"
      }

      "Boolean shouldNotBeFalse should not fail for null" {
         null.shouldNotBeFalse()
      }

      "Boolean shouldNotBeTrue should not fail for false booleans" {
         false.shouldNotBeTrue()
         (3 + 3 == 7).shouldNotBeTrue()
      }

      "Boolean shouldNotBeTrue should fail for true booleans" {
         shouldThrow<AssertionError> {
            true.shouldNotBeTrue()
         }.message shouldBe "true should not equal true"

         shouldThrow<AssertionError> {
            (3 + 3 == 6).shouldNotBeTrue()
         }.message shouldBe "true should not equal true"
      }

      "Boolean shouldNotBeTrue should not fail for null" {
         null.shouldNotBeTrue()
      }
   }
}
