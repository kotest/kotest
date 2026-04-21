package com.sksamuel.kotest.matchers.char

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.char.beDigit
import io.kotest.matchers.char.beLetter
import io.kotest.matchers.char.beLetterOrDigit
import io.kotest.matchers.char.beWhitespace
import io.kotest.matchers.char.shouldBeDigit
import io.kotest.matchers.char.shouldBeLetter
import io.kotest.matchers.char.shouldBeLetterOrDigit
import io.kotest.matchers.char.shouldBeWhitespace
import io.kotest.matchers.char.shouldNotBeDigit
import io.kotest.matchers.char.shouldNotBeLetter
import io.kotest.matchers.char.shouldNotBeLetterOrDigit
import io.kotest.matchers.char.shouldNotBeWhitespace
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

class ValueTest : FreeSpec({

   "char should beLetter()" - {
      "should test that a character is a letter" {
         'A' should beLetter()
         'a'.shouldBeLetter()
         ' ' shouldNot beLetter()
         '\0'.shouldNotBeLetter()
         '0'.shouldNotBeLetter()
         '~'.shouldNotBeLetter()
      }
      "should support chars" {
         val ch = 'A'
         ch.shouldBeLetter()

         val ch2 = '0'
         ch2.shouldNotBeLetter()
      }
      "should support nullable chars" {
         val ch: Char? = 'A'
         ch.shouldBeLetter()

         val ch2: Char? = '0'
         ch2.shouldNotBeLetter()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beLetter()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeLetter()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beLetter()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeLetter()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

   "char should beDigit()" - {
      "should test that a character is a digit" {
         '0' should beDigit()
         '1'.shouldBeDigit()
         'A' shouldNot beDigit()
         'a'.shouldNotBeDigit()
         ' '.shouldNotBeDigit()
         '~'.shouldNotBeDigit()
      }
      "should support chars" {
         val ch = '0'
         ch.shouldBeDigit()

         val ch2 = 'A'
         ch2.shouldNotBeDigit()
      }
      "should support nullable chars" {
         val ch: Char? = '0'
         ch.shouldBeDigit()

         val ch2: Char? = 'A'
         ch2.shouldNotBeDigit()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeDigit()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

   "char should beLetterOrDigit()" - {
      "should test that a character is a letter or digit" {
         '0' should beLetterOrDigit()
         '1'.shouldBeLetterOrDigit()
         'A'.shouldBeLetterOrDigit()
         'a'.shouldBeLetterOrDigit()
         ' ' shouldNot beLetterOrDigit()
         '~'.shouldNotBeLetterOrDigit()
      }
      "should support chars" {
         val ch = '0'
         ch.shouldBeLetterOrDigit()

         val ch2 = ' '
         ch2.shouldNotBeLetterOrDigit()
      }
      "should support nullable chars" {
         val ch: Char? = '0'
         ch.shouldBeLetterOrDigit()

         val ch2: Char? = ' '
         ch2.shouldNotBeLetterOrDigit()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beLetterOrDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeLetterOrDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beLetterOrDigit()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeLetterOrDigit()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

   "char should beWhitespace()" - {
      "should test that a character is whitespace" {
         ' ' should beWhitespace()
         '\0'.shouldBeWhitespace()
         'A' shouldNot beWhitespace()
         'a'.shouldNotBeWhitespace()
         '0'.shouldNotBeWhitespace()
         '~'.shouldNotBeWhitespace()
      }
      "should support chars" {
         val ch = ' '
         ch.shouldBeWhitespace()

         val ch2 = 'A'
         ch2.shouldNotBeWhitespace()
      }
      "should support nullable chars" {
         val ch: Char? = ' '
         ch.shouldBeWhitespace()

         val ch2: Char? = 'A'
         ch2.shouldNotBeWhitespace()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beWhitespace()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeWhitespace()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beWhitespace()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeWhitespace()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

})
