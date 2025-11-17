package com.sksamuel.kotest.matchers.boolean

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.beFalse
import io.kotest.matchers.booleans.beTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldNotBeFalse
import io.kotest.matchers.booleans.shouldNotBeTrue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

@Suppress("KotlinConstantConditions")
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

      "beTrue matcher should match true and not match false" {
         true should beTrue()
         false shouldNot beTrue()
      }

      "beTrue matcher should throw" {
         shouldThrow<AssertionError> {
            false should beTrue()
         }
         shouldThrow<AssertionError> {
            true shouldNot beTrue()
         }
         shouldThrow<AssertionError> {
            null should beTrue()
         }
      }

      "beFalse matcher should match false and not match true" {
         false should beFalse()
         true shouldNot beFalse()
      }

      "beFalse matcher should throw" {
         shouldThrow<AssertionError> {
            true should beFalse()
         }
         shouldThrow<AssertionError> {
            false shouldNot beFalse()
         }
         shouldThrow<AssertionError> {
            null should beFalse()
         }
      }
   }
}
