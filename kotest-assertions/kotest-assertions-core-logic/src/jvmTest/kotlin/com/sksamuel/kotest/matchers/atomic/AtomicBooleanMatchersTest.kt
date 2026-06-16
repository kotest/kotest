package com.sksamuel.kotest.matchers.atomic

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.atomic.shouldBeFalse
import io.kotest.matchers.atomic.shouldBeTrue
import io.kotest.matchers.atomic.shouldNotBeFalse
import io.kotest.matchers.atomic.shouldNotBeTrue
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("SimplifyBooleanWithConstants")
class AtomicBooleanMatchersTest : FreeSpec() {

   init {
      "Boolean shouldBeTrue should not fail for true booleans" {
         AtomicBoolean(true).shouldBeTrue()
         AtomicBoolean(3 + 3 == 6).shouldBeTrue()
      }

      "Boolean shouldBeTrue should fail for false booleans" {
         shouldThrow<AssertionError> { AtomicBoolean(false).shouldBeTrue() }
      }

      "Boolean shouldBeFalse should not fail for false booleans" {
         AtomicBoolean(false).shouldBeFalse()
         AtomicBoolean(3 + 3 == 42).shouldBeFalse()
      }

      "Boolean shouldBeFalse should fail for true booleans" {
         shouldThrow<AssertionError> { AtomicBoolean(true).shouldBeFalse() }
      }

      "Boolean shouldNotBeFalse should not fail for true booleans" {
         AtomicBoolean(true).shouldNotBeFalse()
         AtomicBoolean(3 + 3 == 6).shouldNotBeFalse()
      }

      "Boolean shouldNotBeFalse should fail for false booleans" {
         shouldThrow<AssertionError> {
            AtomicBoolean(false).shouldNotBeFalse()
         }.message shouldBe "false should not equal false"

         shouldThrow<AssertionError> {
            AtomicBoolean(3 + 3 == 7).shouldNotBeFalse()
         }.message shouldBe "false should not equal false"
      }

      "Boolean shouldNotBeTrue should not fail for false booleans" {
         AtomicBoolean(false).shouldNotBeTrue()
         AtomicBoolean(3 + 3 == 7).shouldNotBeTrue()
      }

      "Boolean shouldNotBeTrue should fail for true booleans" {
         shouldThrow<AssertionError> {
            AtomicBoolean(true).shouldNotBeTrue()
         }.message shouldBe "true should not equal true"

         shouldThrow<AssertionError> {
            AtomicBoolean(3 + 3 == 6).shouldNotBeTrue()
         }.message shouldBe "true should not equal true"
      }
   }
}
