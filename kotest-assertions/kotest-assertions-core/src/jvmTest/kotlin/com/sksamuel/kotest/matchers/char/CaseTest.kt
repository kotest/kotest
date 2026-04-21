package com.sksamuel.kotest.matchers.char

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.char.beLowerCaseChar
import io.kotest.matchers.char.beTitleCaseChar
import io.kotest.matchers.char.beUpperCaseChar
import io.kotest.matchers.char.shouldBeLowerCaseChar
import io.kotest.matchers.char.shouldBeTitleCaseChar
import io.kotest.matchers.char.shouldBeUpperCaseChar
import io.kotest.matchers.char.shouldNotBeLowerCaseChar
import io.kotest.matchers.char.shouldNotBeTitleCaseChar
import io.kotest.matchers.char.shouldNotBeUpperCaseChar
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

class CaseTest : FreeSpec({

   "char should beUpperCaseChar()" - {
      "should test that a character is upper case" {
         'A' should beUpperCaseChar()
         'a' shouldNot beUpperCaseChar()
         'B'.shouldBeUpperCaseChar()
         'b'.shouldNotBeUpperCaseChar()
      }
      "should support chars" {
         val ch = 'A'
         ch.shouldBeUpperCaseChar()

         val ch2 = 'a'
         ch2.shouldNotBeUpperCaseChar()
      }
      "should support nullable chars" {
         val ch: Char? = 'A'
         ch.shouldBeUpperCaseChar()

         val ch2: Char? = 'a'
         ch2.shouldNotBeUpperCaseChar()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beUpperCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeUpperCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beUpperCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeUpperCaseChar()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

   "char should beLowerCaseChar()" - {
      "should test that a character is lower case" {
         'a' should beLowerCaseChar()
         'A' shouldNot beLowerCaseChar()
         'b'.shouldBeLowerCaseChar()
         'B'.shouldNotBeLowerCaseChar()
      }
      "should support chars" {
         val ch = 'a'
         ch.shouldBeLowerCaseChar()

         val ch2 = 'A'
         ch2.shouldNotBeLowerCaseChar()
      }
      "should support nullable chars" {
         val ch: Char? = 'a'
         ch.shouldBeLowerCaseChar()

         val ch2: Char? = 'A'
         ch2.shouldNotBeLowerCaseChar()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beLowerCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeLowerCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beLowerCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeLowerCaseChar()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

   "char should beTitleCaseChar()" - {
      "should test that a character is title case" {
         'A' should beTitleCaseChar()
         'a' shouldNot beTitleCaseChar()
         'B'.shouldBeTitleCaseChar()
         'b'.shouldNotBeTitleCaseChar()
      }
      "should support chars" {
         val ch = 'A'
         ch.shouldBeTitleCaseChar()

         val ch2 = 'a'
         ch2.shouldNotBeTitleCaseChar()
      }
      "should support nullable chars" {
         val ch: Char? = 'A'
         ch.shouldBeTitleCaseChar()

         val ch2: Char? = 'a'
         ch2.shouldNotBeTitleCaseChar()
      }
      "should fail if value is null" {
         shouldThrow<AssertionError> {
            null shouldNot beTitleCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldNotBeTitleCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should beTitleCaseChar()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null.shouldBeTitleCaseChar()
         }.message shouldBe "Expecting actual not to be null"
      }
   }

})
