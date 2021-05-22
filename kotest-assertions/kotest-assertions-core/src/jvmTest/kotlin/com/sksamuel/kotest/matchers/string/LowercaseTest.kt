package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beLowerCase
import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldNotBeLowerCase

class LowercaseTest : FreeSpec({

   "string should beLowerCase()" - {
      "should test that a string is lower case" {
         "" should beLowerCase()
         "hello" should beLowerCase()
         "HELLO" shouldNot beLowerCase()
         "HELlo" shouldNot beLowerCase()

         "hello".shouldBeLowerCase()
         "HELLO".shouldNotBeLowerCase()
      }
      "should support char seqs" {
         val cs = "HELLO"
         cs.shouldNotBeLowerCase()

         val cs2 = "hello"
         cs2.shouldBeLowerCase()
      }
      "should support nullable char seqs" {
         val cs: CharSequence? = "HELLO"
         cs.shouldNotBeLowerCase()

         val cs2: CharSequence? = "hello"
         cs2.shouldBeLowerCase()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beLowerCase()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeLowerCase()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beLowerCase()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeLowerCase()
         }.message shouldBe "Expecting actual not to be null"
      }
   }
})
