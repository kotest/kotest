package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.string.beEmpty
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containADigit
import io.kotest.matchers.string.containIgnoringCase
import io.kotest.matchers.string.containOnlyDigits
import io.kotest.matchers.string.containOnlyOnce
import io.kotest.matchers.string.containOnlyWhitespace
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.haveSameLengthAs
import io.kotest.matchers.string.match
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.string.shouldBeInteger
import io.kotest.matchers.string.shouldBeSingleLine
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainADigit
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldContainOnlyDigits
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.matchers.string.shouldHaveLengthIn
import io.kotest.matchers.string.shouldHaveSameLengthAs
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeEqualIgnoringCase
import io.kotest.matchers.string.shouldNotBeSingleLine
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldNotContainADigit
import io.kotest.matchers.string.shouldNotContainIgnoringCase
import io.kotest.matchers.string.shouldNotContainOnlyDigits
import io.kotest.matchers.string.shouldNotContainOnlyOnce
import io.kotest.matchers.string.shouldNotEndWith
import io.kotest.matchers.string.shouldNotHaveLengthBetween
import io.kotest.matchers.string.shouldNotHaveLengthIn
import io.kotest.matchers.string.shouldNotHaveSameLengthAs
import io.kotest.matchers.string.shouldNotMatch

class StringMatchersTest : FreeSpec() {
   init {

      "string shouldBe other" - {
         "should support null arguments" {
            val a: String? = "a"
            val b: String? = "a"
            a shouldBe b
         }

         "should report when only line endings differ" {
            forAll(
               row("a\nb", "a\r\nb"),
               row("a\nb\nc", "a\nb\r\nc"),
               row("a\r\nb", "a\nb"),
               row("a\nb", "a\rb"),
               row("a\rb", "a\r\nb")
            ) { expected, actual ->
               shouldThrow<AssertionError> {
                  actual shouldBe expected
               }.let {
                  it.message shouldContain "contents match, but line-breaks differ"
               }
            }
         }

         "should show diff when newline count differs" {
            shouldThrow<AssertionError> {
               "a\nb" shouldBe "a\n\nb"
            }.message shouldBe """
               |(contents match, but line-breaks differ; output has been escaped to show line-breaks)
               |expected:<a\n\nb> but was:<a\nb>
               """.trimMargin()
         }
      }

      "contain only once" {
         "la tour" should containOnlyOnce("tour")
         "la tour tour" shouldNot containOnlyOnce("tour")
         "la tour tour" shouldNotContainOnlyOnce "tour"

         shouldThrow<AssertionError> {
            "la" should containOnlyOnce("tour")
         }.message shouldBe """"la" should contain the substring "tour" exactly once, but did not contain it"""

         shouldThrow<AssertionError> {
            "Run, Forrest, Run" should containOnlyOnce("Run")
         }.message shouldBe """"Run, Forrest, Run" should contain the substring "Run" exactly once, but contained it at least at indexes 0 and 14"""

         shouldThrow<AssertionError> {
            null shouldNot containOnlyOnce("tour")
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null shouldNotContainOnlyOnce "tour"
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should containOnlyOnce("tour")
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null shouldContainOnlyOnce "tour"
         }.message shouldBe "Expecting actual not to be null"
      }

      "contain(regex)" {
         "la tour" should contain("^.*?tour$".toRegex())
         "la tour" shouldNot contain(".*?abc.*?".toRegex())

         "la tour" shouldContain "^.*?tour$".toRegex()
         "la tour" shouldNotContain ".*?abc.*?".toRegex()

         shouldThrow<AssertionError> {
            "la tour" shouldContain ".*?abc.*?".toRegex()
         }.message shouldBe "\"la tour\" should contain regex .*?abc.*?"

         shouldThrow<AssertionError> {
            "la tour" shouldNotContain "^.*?tour$".toRegex()
         }.message shouldBe "\"la tour\" should not contain regex ^.*?tour\$"

         shouldThrow<AssertionError> {
            null shouldNot contain("^.*?tour$".toRegex())
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null shouldNotContain "^.*?tour$".toRegex()
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null should contain("^.*?tour$".toRegex())
         }.message shouldBe "Expecting actual not to be null"

         shouldThrow<AssertionError> {
            null shouldContain "^.*?tour$".toRegex()
         }.message shouldBe "Expecting actual not to be null"
      }

      "string should beEmpty()" - {
         "should test that a string has length 0" {
            "" should beEmpty()
            "hello" shouldNot beEmpty()
            "hello".shouldNotBeEmpty()
            "".shouldBeEmpty()

            shouldThrow<AssertionError> {
               "hello".shouldBeEmpty()
            }.message shouldBe "\"hello\" should be empty"

            shouldThrow<AssertionError> {
               "".shouldNotBeEmpty()
            }.message shouldBe "<empty string> should not be empty"
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot beEmpty()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldNotBeEmpty()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should beEmpty()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldBeEmpty()
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "string should containADigit()" - {
         "should test that a string has at least one number" {
            "" shouldNot containADigit()
            "1" should containADigit()
            "a1".shouldContainADigit()
            "a1b" should containADigit()
            "hello" shouldNot containADigit()
            "hello".shouldNotContainADigit()

            shouldThrow<AssertionError> {
               "hello" should containADigit()
            }.message shouldBe "\"hello\" should contain at least one digit"
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot containADigit()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldNotContainADigit()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should containADigit()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldContainADigit()
            }.message shouldBe "Expecting actual not to be null"
         }
         "shouldNotContainADigit should print first digit" {
            shouldThrow<AssertionError> {
               "aww1b2c3" shouldNot containADigit()
            }.message shouldBe "\"aww1b2c3\" should not contain any digits, but contained '1' at index 3"
         }
      }

      "string should beBlank()" - {
         "should test that a string has only whitespace" {
            "" should beBlank()
            "" should containOnlyWhitespace()
            "     \t     " should beBlank()
            "hello" shouldNot beBlank()

            "hello".shouldNotBeBlank()
            "   ".shouldBeBlank()
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot beBlank()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldNotBeBlank()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should beBlank()
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null.shouldBeBlank()
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "string should haveSameLengthAs(other)" - {
         "should test that a string has the same length as another string" {
            "hello" should haveSameLengthAs("world")
            "hello" shouldNot haveSameLengthAs("o")
            "" should haveSameLengthAs("")
            "" shouldNot haveSameLengthAs("o")
            "5" shouldNot haveSameLengthAs("")

            "" shouldHaveSameLengthAs ""
            "qwe" shouldHaveSameLengthAs "sdf"
            "" shouldNotHaveSameLengthAs "qweqweqe"
            "qe" shouldNotHaveSameLengthAs ""
            "qe" shouldNotHaveSameLengthAs "fffff"
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot haveSameLengthAs("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotHaveSameLengthAs ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should haveSameLengthAs("o")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldHaveSameLengthAs "o"
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "string should containIgnoringCase(other)" - {
         "should test that a string contains another string ignoring case" {
            "hello" should containIgnoringCase("HELLO")
            "hello" shouldNot containIgnoringCase("hella")

            "hello" shouldContainIgnoringCase "HEllO"
            "hello" shouldNotContainIgnoringCase "hella"
            shouldThrow<AssertionError> {
               "apples and oranges" shouldNotContainIgnoringCase "ORANGE"
            }.message shouldBe "\"apples and oranges\" should not contain the substring \"ORANGE\" (case insensitive), but contained it at index 11"
         }

         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null shouldNot containIgnoringCase("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotContainIgnoringCase ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null should containIgnoringCase("o")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldContainIgnoringCase "o"
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "should containOnlyDigits()" - {
         "should test that a string only contains 0-9" {
            "hello" shouldNot containOnlyDigits()
            "123123" should containOnlyDigits()
            "" should containOnlyDigits()
            "aa123" shouldNot containOnlyDigits()

            "123".shouldContainOnlyDigits()
            "qweqwe123".shouldNotContainOnlyDigits()
            "qweqwe".shouldNotContainOnlyDigits()
            "123a".shouldNotContainOnlyDigits()
         }
         "should print first non-digit" {
            shouldThrow<AssertionError> {
               "123a234e".shouldContainOnlyDigits()
            }.message shouldBe """"123a234e" should contain only digits, but contained 'a' at index 3"""
         }
      }



      "Matchers should end with x" - {
         "should fail if string does not end with x" {
            "bibble" should endWith("ble")

            shouldThrow<AssertionError> {
               "bibble" should endWith("qwe")
            }
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should endWith("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot endWith("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldEndWith ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotEndWith ""
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "String should match regex" - {
         "should test string matches regular expression" {
            "sam" should match("sam")
            "bibble" should match("bibb..")
            "foo" should match(".*")
            "foo" shouldMatch ".*"
            "foo" shouldMatch "foo"
            "foo" shouldMatch "f.."
            "boo" shouldNotMatch "foo"
            "boo" shouldNotMatch "f.."
         }
         "should fail if value is null" {
            shouldThrow<AssertionError> {
               null should match("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNot match("")
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldMatch ""
            }.message shouldBe "Expecting actual not to be null"

            shouldThrow<AssertionError> {
               null shouldNotMatch ""
            }.message shouldBe "Expecting actual not to be null"
         }
      }

      "should be equal ignoring case" - {
         "should match equal strings" {
            "foo" shouldBeEqualIgnoringCase "foo"
            "BAR" shouldBeEqualIgnoringCase "BAR"
            "123" shouldBeEqualIgnoringCase "123"
         }

         "Should match strings that are equal ignoring case" {
            "FOO" shouldBeEqualIgnoringCase "fOo"
            "BaR" shouldBeEqualIgnoringCase "bar"
            "123aBC" shouldBeEqualIgnoringCase "123abc"
         }

         "Should not match strings that are different, but in same case" {
            shouldThrow<AssertionError> { "123" shouldBeEqualIgnoringCase "321" }
            shouldThrow<AssertionError> { "FOO" shouldBeEqualIgnoringCase "FOOO" }
            shouldThrow<AssertionError> { "bar" shouldBeEqualIgnoringCase "baar" }
         }

         "Should not match strings that are different, but in different case" {
            shouldThrow<AssertionError> { "FOO" shouldBeEqualIgnoringCase "fooo" }
            shouldThrow<AssertionError> { "bar" shouldBeEqualIgnoringCase "BAAR" }
         }
      }

      "should not be equal ignoring case" - {
         "should not match equal strings" {
            shouldThrow<AssertionError> { "foo" shouldNotBeEqualIgnoringCase "foo" }
            shouldThrow<AssertionError> { "BAR" shouldNotBeEqualIgnoringCase "BAR" }
            shouldThrow<AssertionError> { "123" shouldNotBeEqualIgnoringCase "123" }
         }

         "Should not match strings that are equal ignoring case" {
            shouldThrow<AssertionError> { "FOO" shouldNotBeEqualIgnoringCase "fOo" }
            shouldThrow<AssertionError> { "BaR" shouldNotBeEqualIgnoringCase "bar" }
            shouldThrow<AssertionError> { "123aBC" shouldNotBeEqualIgnoringCase "123abc" }
         }

         "Should match strings that are different, but in same case" {
            "123" shouldNotBeEqualIgnoringCase "321"
            "FOO" shouldNotBeEqualIgnoringCase "FOOO"
            "bar" shouldNotBeEqualIgnoringCase "baar"
         }

         "Should match strings that are different, but in different case" {
            "FOO" shouldNotBeEqualIgnoringCase "fooo"
            "bar" shouldNotBeEqualIgnoringCase "BAAR"
         }
      }

      "should have single line" - {
         "should work for single lines" {
            "hello".shouldBeSingleLine()
            shouldThrow<AssertionError> { "FOO".shouldNotBeSingleLine() }
         }
         "should fail for multi lines" {
            "hello\nworld".shouldNotBeSingleLine()
            shouldThrow<AssertionError> { "hello\nworld".shouldBeSingleLine() }
         }
      }

      "should have length between x and y" - {
         "should work when x == y" {
            "hello".shouldHaveLengthBetween(5, 5)
            "hello".shouldHaveLengthBetween(5..5)
            "hello".shouldNotHaveLengthBetween(10, 12)
            "hello".shouldNotHaveLengthBetween(10..12)
         }

         "should work when x != y" {
            "hello".shouldHaveLengthBetween(3, 7)
         }

         "should throw error when invalid" {
            shouldThrow<AssertionError> { "FOO".shouldHaveLengthBetween(9, 10) }
            shouldThrow<AssertionError> { "FOO".shouldNotHaveLengthBetween(2, 5) }
         }

         "should throw error when x > y" {
            shouldThrow<IllegalArgumentException> { "FOO".shouldHaveLengthBetween(11, 10) }
         }
      }

      "should have length in range" - {
         "should work for range" {
            "hello".shouldHaveLengthIn(5..5)
            "hello".shouldHaveLengthIn(3..10)
            "hello".shouldHaveLengthIn(5..10)
            "hello".shouldNotHaveLengthIn(10..12)
            "hello".shouldNotHaveLengthIn(1..4)
         }

         "should fail when outside range" {
            shouldThrow<AssertionError> { "FOO".shouldHaveLengthIn(9..10) }
         }
      }

      "should be integer" - {
         "should return integer for integer string" {
            "123123".shouldBeInteger() shouldBe 123123
            "0".shouldBeInteger() shouldBe 0
            "1".shouldBeInteger() shouldBe 1
            "-9876".shouldBeInteger() shouldBe -9876

            "BABE".shouldBeInteger(16) shouldBe 0xBABE
            "-babe".shouldBeInteger(16) shouldBe -0xBABE

            "100".shouldBeInteger(2) shouldBe 0b100
            "-100".shouldBeInteger(2) shouldBe -0b100

            "0124".shouldBeInteger() shouldBe 124
         }

         "should fail for null" {
            shouldThrow<AssertionError> { null.shouldBeInteger() }
         }

         "should fail for non-integer string" {
            shouldThrow<AssertionError> { " 123123".shouldBeInteger() }
            shouldThrow<AssertionError> { "0 ".shouldBeInteger() }
            shouldThrow<AssertionError> { "1-".shouldBeInteger() }
            shouldThrow<AssertionError> { "-9876.0".shouldBeInteger() }
            shouldThrow<AssertionError> { "fail".shouldBeInteger() }

            shouldThrow<AssertionError> { "BABEg".shouldBeInteger(16) }
            shouldThrow<AssertionError> { "-baGbe".shouldBeInteger(16) }
         }

         "should fail for overflowing integer strings" {
            shouldThrow<AssertionError> { Int.MAX_VALUE.toLong().plus(1).toString().shouldBeInteger() }
            shouldThrow<AssertionError> { Int.MIN_VALUE.toLong().minus(1).toString().shouldBeInteger() }
         }

         "should fail with a proper exception for bad radix" {
            shouldThrow<IllegalArgumentException> { "1".shouldBeInteger(1) }
            shouldThrow<IllegalArgumentException> { "11".shouldBeInteger(100) }
         }

         "should make smart cast of receiver to non-null string" {
            fun use(string: String) {}

            val value: String? = "456"
            value.shouldBeInteger()
            use(value)  // if this is compiled, then smart cast works
         }
      }
   }
}
