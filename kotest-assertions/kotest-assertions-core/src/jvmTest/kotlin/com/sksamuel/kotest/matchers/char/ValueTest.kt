package com.sksamuel.kotest.matchers.char

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
         '\n'.shouldNotBeLetter()
         '0'.shouldNotBeLetter()
         '~'.shouldNotBeLetter()
         '\u0000'.shouldNotBeLetter()
      }
      "should support chars" {
         val ch = 'A'
         ch.shouldBeLetter()

         val ch2 = '0'
         ch2.shouldNotBeLetter()
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
   }

   "char should beWhitespace()" - {
      "should test that a character is whitespace" {
         ' ' should beWhitespace()
         '\n'.shouldBeWhitespace()
         'A' shouldNot beWhitespace()
         'a'.shouldNotBeWhitespace()
         '0'.shouldNotBeWhitespace()
         '~'.shouldNotBeWhitespace()
         '\u0000'.shouldNotBeWhitespace()
      }
      "should support chars" {
         val ch = ' '
         ch.shouldBeWhitespace()

         val ch2 = 'A'
         ch2.shouldNotBeWhitespace()
      }
   }

})
