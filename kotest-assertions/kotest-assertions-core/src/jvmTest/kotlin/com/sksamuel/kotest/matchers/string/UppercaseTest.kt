package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beUpperCase
import io.kotest.matchers.string.shouldBeUpperCase
import io.kotest.matchers.string.shouldNotBeUpperCase

class UppercaseTest : FreeSpec({

   "string should beUpperCase()" - {
      "should test that a string is upper case" {
         "" should beUpperCase()
         "HELLO" should beUpperCase()
         "heLLO" shouldNot beUpperCase()
         "hello" shouldNot beUpperCase()
         "HELLO".shouldBeUpperCase()
         "HelLO".shouldNotBeUpperCase()
      }
      "should support char seqs" {
         val cs = "HELLO"
         cs.shouldBeUpperCase()

         val cs2 = "hello"
         cs2.shouldNotBeUpperCase()
      }
      "should support nullable char seqs" {
         val cs: CharSequence? = "HELLO"
         cs.shouldBeUpperCase()

         val cs2: CharSequence? = "hello"
         cs2.shouldNotBeUpperCase()
      }
      "should fail if value is null" {
         io.kotest.assertions.throwables.shouldThrow<AssertionError> {
            null shouldNot io.kotest.matchers.string.beUpperCase()
         }.message shouldBe "Expecting actual not to be null"

         io.kotest.assertions.throwables.shouldThrow<AssertionError> {
            null.shouldNotBeUpperCase()
         }.message shouldBe "Expecting actual not to be null"

         io.kotest.assertions.throwables.shouldThrow<AssertionError> {
            null should io.kotest.matchers.string.beUpperCase()
         }.message shouldBe "Expecting actual not to be null"

         io.kotest.assertions.throwables.shouldThrow<AssertionError> {
            null.shouldBeUpperCase()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

})
