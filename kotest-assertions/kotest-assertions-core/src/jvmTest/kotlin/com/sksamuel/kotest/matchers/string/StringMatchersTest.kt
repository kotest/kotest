package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.string.beEmpty
import io.kotest.matchers.string.beFalsy
import io.kotest.matchers.string.beLowerCase
import io.kotest.matchers.string.beTruthy
import io.kotest.matchers.string.beUpperCase
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containADigit
import io.kotest.matchers.string.containInOrder
import io.kotest.matchers.string.containIgnoringCase
import io.kotest.matchers.string.containOnlyDigits
import io.kotest.matchers.string.containOnlyOnce
import io.kotest.matchers.string.containOnlyWhitespace
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.haveLineCount
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.haveMinLength
import io.kotest.matchers.string.haveSameLengthAs
import io.kotest.matchers.string.haveSubstring
import io.kotest.matchers.string.include
import io.kotest.matchers.string.match
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.string.shouldBeFalsy
import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldBeSingleLine
import io.kotest.matchers.string.shouldBeTruthy
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.matchers.string.shouldBeUpperCase
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainADigit
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldContainOnlyDigits
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.matchers.string.shouldHaveLengthIn
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldHaveSameLengthAs
import io.kotest.matchers.string.shouldInclude
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeEqualIgnoringCase
import io.kotest.matchers.string.shouldNotBeLowerCase
import io.kotest.matchers.string.shouldNotBeSingleLine
import io.kotest.matchers.string.shouldNotBeUUID
import io.kotest.matchers.string.shouldNotBeUpperCase
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldNotContainADigit
import io.kotest.matchers.string.shouldNotContainInOrder
import io.kotest.matchers.string.shouldNotContainIgnoringCase
import io.kotest.matchers.string.shouldNotContainOnlyDigits
import io.kotest.matchers.string.shouldNotContainOnlyOnce
import io.kotest.matchers.string.shouldNotEndWith
import io.kotest.matchers.string.shouldNotHaveLength
import io.kotest.matchers.string.shouldNotHaveLengthBetween
import io.kotest.matchers.string.shouldNotHaveLengthIn
import io.kotest.matchers.string.shouldNotHaveLineCount
import io.kotest.matchers.string.shouldNotHaveMaxLength
import io.kotest.matchers.string.shouldNotHaveMinLength
import io.kotest.matchers.string.shouldNotHaveSameLengthAs
import io.kotest.matchers.string.shouldNotInclude
import io.kotest.matchers.string.shouldNotMatch
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.string.startWith
import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.string
import io.kotest.properties.uuid
import io.kotest.data.row
import org.opentest4j.AssertionFailedError

class StringMatchersTest : FreeSpec() {
  init {

    "string shouldBe other" - {
      "should support null arguments" {
        val a: String? = "a"
        val b: String? = "a"
        a shouldBe b
      }

      "should use junit5 assertion type when available" {
        shouldThrow<AssertionFailedError> {
          "a" shouldBe "b"
        }.let {
          it.actual.value shouldBe "\"a\""
          it.expected.value shouldBe "\"b\""
        }
      }

      "should report when only line endings differ" {
        forAll(
          row("a\nb", "a\r\nb"),
          row("a\nb\nc", "a\nb\r\nc"),
          row("a\r\nb", "a\nb"),
          row("a\nb", "a\rb"),
          row("a\rb", "a\r\nb")
        ) { expected, actual  ->
          shouldThrow<AssertionFailedError> {
            actual shouldBe expected
          }.let {
            it.actual.value shouldBe "\"$actual\""
            it.expected.value shouldBe "\"$expected\""
            it.message shouldBe "line contents match, but line-break characters differ"
          }
        }
      }

      "should show diff when newline count differs" {
        shouldThrow<AssertionFailedError> {
          "a\nb" shouldBe "a\n\nb"
        }.let {
          it.actual.value shouldBe "\"a\nb\""
          it.expected.value shouldBe "\"a\n\nb\""
          it.message should startWith("expected: \"a")
        }
      }
    }

    "contain only once" {
      "la tour" should containOnlyOnce("tour")
      "la tour tour" shouldNot containOnlyOnce("tour")
      "la tour tour" shouldNotContainOnlyOnce "tour"

      shouldThrow<AssertionError> {
        "la" should containOnlyOnce("tour")
      }.message shouldBe "la should contain the substring tour exactly once"

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
      }.message shouldBe "la tour should contain regex .*?abc.*?"

      shouldThrow<AssertionError> {
        "la tour" shouldNotContain "^.*?tour$".toRegex()
      }.message shouldBe "la tour should not contain regex ^.*?tour\$"

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

    "string should contain" - {
      "should test that a string contains substring" {
        "hello" should include("h")
        "hello" shouldInclude "o"
        "hello" should include("ell")
        "hello" should include("hello")
        "hello" should include("")
        "la tour" shouldContain "tour"

        shouldThrow<AssertionError> {
          "la tour" shouldContain "wibble"
        }.message shouldBe "la tour should include substring wibble"

        shouldThrow<AssertionError> {
          "hello" should include("allo")
        }.message shouldBe "hello should include substring allo"

        shouldThrow<AssertionError> {
          "hello" shouldInclude "qwe"
        }.message shouldBe "hello should include substring qwe"
      }

      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null shouldNot include("allo")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotInclude "qwe"
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot contain("allo")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotContain "qwe"
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should include("allo")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should haveSubstring("allo")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldInclude "qwe"
        }.message shouldBe "Expecting actual not to be null"
      }
    }

    "string should beEmpty()" - {
      "should test that a string has length 0" {
        "" should beEmpty()
        "hello" shouldNot beEmpty()
        "hello".shouldNotBeEmpty()
        "".shouldBeEmpty()

        shouldThrow<AssertionError> {
          "hello".shouldBeEmpty()
        }.message shouldBe "hello should be empty"

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
        }.message shouldBe "hello should contain at least one digit"
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
    }


    "string should containInOrder()" - {
      "should test that a string contains the requested strings" {
        "a" should containInOrder()
        "a" shouldNot containInOrder("d")
        "ab" should containInOrder("a", "b")
        "ab" shouldNot containInOrder("b", "a")
        "azc" should containInOrder("a", "c")
        "zabzc" should containInOrder("ab", "c")
        "a" shouldNot containInOrder("a", "a")
        "aa" should containInOrder("a", "a")
        "azbzbzc" should containInOrder("a", "b", "b", "c")
        "abab" should containInOrder("a", "b", "a", "b")
        "ababa" should containInOrder("aba", "aba")
        "aaa" should containInOrder("aa", "aa")
        "" should containInOrder()
        "" shouldNot containInOrder("a")
        "" should containInOrder("")
        "" should containInOrder("", "")
        "ab" should containInOrder("", "a", "", "b", "")
      }

      "should print the substrings in matcher messages" {
        shouldThrow<AssertionError> {
          "zhelzloz" should containInOrder("hel", "no", "lo")
        }.message shouldBe "zhelzloz should include substrings [\"hel\", \"no\", \"lo\"] in order"

        shouldThrow<AssertionError> {
          "zhelznozloz" shouldNot containInOrder("hel", "no", "lo")
        }.message shouldBe "zhelznozloz should not include substrings [\"hel\", \"no\", \"lo\"] in order"
      }

      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null shouldNot containInOrder("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null.shouldNotContainInOrder("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should containInOrder("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null.shouldContainInOrder("")
        }.message shouldBe "Expecting actual not to be null"
      }
    }


    "string should beUpperCase()" - {
      "should test that a string is upper case" {
        "" should beUpperCase()
        "HELLO" should beUpperCase()
        "heLLO" shouldNot beUpperCase()
        "hello" shouldNot beUpperCase()
        "HELLO".shouldBeUpperCase()
        "HelLO".shouldNotBeUpperCase()
      }

      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null shouldNot beUpperCase()
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null.shouldNotBeUpperCase()
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should beUpperCase()
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null.shouldBeUpperCase()
        }.message shouldBe "Expecting actual not to be null"
      }
    }


    "string should beLowerCase()" - {
      "should test that a string is lower case" {
        "" should beLowerCase()
        "hello" should beLowerCase()
        "HELLO" shouldNot beLowerCase()
        "HELlo" shouldNot beLowerCase()

        "hello".shouldBeLowerCase()
        "HELLO".shouldNotBeLowerCase()
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
    }

    "should endWith" - {
      "should test strings" {
        "hello" should endWith("o")
        "hello" should endWith("")
        "hello" shouldEndWith ""
        "hello" shouldEndWith "lo"
        "hello" shouldEndWith "o"
        "hello" shouldNotEndWith "w"
        "" should endWith("")
        shouldThrow<AssertionError> {
          "" should endWith("h")
        }
        shouldThrow<AssertionError> {
          "hello" should endWith("goodbye")
        }
      }

      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null shouldNot endWith("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotEndWith ""
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should endWith("o")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldEndWith "o"
        }.message shouldBe "Expecting actual not to be null"
      }
    }

    "should startWith" - {
      "should test strings" {
        "hello" should startWith("h")
        "hello" should startWith("")
        "hello" shouldStartWith ""
        "hello" shouldStartWith "h"
        "hello" shouldStartWith "he"
        "hello" shouldNotStartWith "w"
        "hello" shouldNotStartWith "wo"
        "" should startWith("")
        shouldThrow<AssertionError> {
          "" should startWith("h")
        }
        shouldThrow<AssertionError> {
          "hello" should startWith("goodbye")
        }
      }
      "should show divergence in error message" {
        shouldThrow<AssertionError> {
          "la tour eiffel" should startWith("la tour tower london")
        }.message shouldBe "la tour eiffel should start with la tour tower london (diverged at index 8)"
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should startWith("h")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldStartWith ""
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot startWith("h")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotStartWith "w"
        }.message shouldBe "Expecting actual not to be null"
      }
    }

    "should haveLength(5)" - {
      "should compare length of string" {
        "bibble" should haveLength(6)
        "" should haveLength(0)
        "" shouldHaveLength 0
        "hello" shouldNotHaveLength 3
        "hello" shouldHaveLength 5
        shouldThrow<AssertionError> {
          "" should haveLength(3)
        }.message shouldBe "<empty string> should have length 3, but instead was 0"
        shouldThrow<AssertionError> {
          "" shouldHaveLength 3
        }.message shouldBe "<empty string> should have length 3, but instead was 0"
        shouldThrow<AssertionError> {
          "hello" shouldHaveLength 3
        }.message shouldBe "hello should have length 3, but instead was 5"
        shouldThrow<AssertionError> {
          "hello" shouldNotHaveLength 5
        }.message shouldBe "hello should not have length 5"
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should haveLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null should haveLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot haveLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotHaveLength 0
        }.message shouldBe "Expecting actual not to be null"
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
    "Matchers should have substring x" - {
      "should test string contains substring" {
        "bibble" should include("")
        "bibble" should include("bb")
        "bibble" should include("bibble")
      }
      "should fail if string does not contains substring" {
        shouldThrow<AssertionError> {
          "bibble" should include("qweqwe")
        }
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should include("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot include("")
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldInclude ""
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotInclude ""
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
    "should have line count" - {
      "should count all newlines" {
        "" should haveLineCount(0)
        "".shouldHaveLineCount(0)
        "\n" should haveLineCount(2)
        "\n" shouldHaveLineCount (2)
        "\r\n" should haveLineCount(2)
        "\r\n".shouldHaveLineCount(2)
        "a\nb\nc" should haveLineCount(3)
        "\r\n" shouldNotHaveLineCount 1
        "\r\n".shouldNotHaveLineCount(3)
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should haveLineCount(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot haveLineCount(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldHaveLineCount 0
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotHaveLineCount 0
        }.message shouldBe "Expecting actual not to be null"
      }
    }
    "should have min length" - {
      "should check min length" {
        "" should haveMinLength(0)
        "1" should haveMinLength(1)
        "123" shouldHaveMinLength 1
        "" shouldNotHaveMinLength 1

        shouldThrow<AssertionError> {
          "1" should haveMinLength(2)
        }.message shouldBe "1 should have minimum length of 2"
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should haveMinLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot haveMinLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldHaveMinLength 0
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotHaveMinLength 0
        }.message shouldBe "Expecting actual not to be null"
      }
    }
    "should have max length" - {
      "should check max length" {
        "" should haveMaxLength(0)
        "1" should haveMaxLength(1)
        "123" shouldHaveMaxLength 10
        "123" shouldNotHaveMaxLength 1

        shouldThrow<AssertionError> {
          "12" should haveMaxLength(1)
        }.message shouldBe "12 should have maximum length of 1"
      }
      "should fail if value is null" {
        shouldThrow<AssertionError> {
          null should haveMaxLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNot haveMaxLength(0)
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldHaveMaxLength 0
        }.message shouldBe "Expecting actual not to be null"

        shouldThrow<AssertionError> {
          null shouldNotHaveMaxLength 0
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
        "hello".shouldNotHaveLengthBetween(10, 12)
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

    "should be truthy" - {
      "should work with proper values" {
        "true".shouldBeTruthy()
        "yes".shouldBeTruthy()
        "y".shouldBeTruthy()
        "1".shouldBeTruthy()
        "Y".shouldBeTruthy()
        "Yes".shouldBeTruthy()
        "YeS".shouldBeTruthy()
        "True".shouldBeTruthy()
        "TrUe".shouldBeTruthy()
        "TRUE".shouldBeTruthy()
      }

      "should fail with unexpected values" {
        shouldThrow<AssertionError> { "false".shouldBeTruthy() }
        shouldThrow<AssertionError> { "no".shouldBeTruthy() }
        shouldThrow<AssertionError> { "n".shouldBeTruthy() }
        shouldThrow<AssertionError> { "0".shouldBeTruthy() }
        shouldThrow<AssertionError> { "whatever".shouldBeTruthy() }
        shouldThrow<AssertionError> { "true yes".shouldBeTruthy() }
        shouldThrow<AssertionError> { "a".shouldBeTruthy() }
        shouldThrow<AssertionError> { "10".shouldBeTruthy() }
        shouldThrow<AssertionError> { "".shouldBeTruthy() }
        shouldThrow<AssertionError> { " ".shouldBeTruthy() }
        shouldThrow<AssertionError> { " ".shouldBeTruthy() }
        shouldThrow<AssertionError> { null.shouldBeTruthy() }
      }

      "should provide error message" {
        shouldThrow<AssertionError> { "false".shouldBeTruthy() }
            .message.shouldBe("false should be equal ignoring case one of values: [true, yes, y, 1]")
        shouldThrow<AssertionError> { "YES" shouldNot beTruthy() }
            .message.shouldBe("YES should not be equal ignoring case one of values: [true, yes, y, 1]")
      }
    }

    "should be falsy" - {
      "should work with proper values" {
        "false".shouldBeFalsy()
        "no".shouldBeFalsy()
        "n".shouldBeFalsy()
        "0".shouldBeFalsy()
        "N".shouldBeFalsy()
        "No".shouldBeFalsy()
        "nO".shouldBeFalsy()
        "False".shouldBeFalsy()
        "FaLse".shouldBeFalsy()
        "FALSE".shouldBeFalsy()
      }

      "should fail with unexpected values" {
        shouldThrow<AssertionError> { "true".shouldBeFalsy() }
        shouldThrow<AssertionError> { "yes".shouldBeFalsy() }
        shouldThrow<AssertionError> { "1".shouldBeFalsy() }
        shouldThrow<AssertionError> { "y".shouldBeFalsy() }
        shouldThrow<AssertionError> { "whatever".shouldBeFalsy() }
        shouldThrow<AssertionError> { "true yes".shouldBeFalsy() }
        shouldThrow<AssertionError> { "a".shouldBeFalsy() }
        shouldThrow<AssertionError> { "10".shouldBeFalsy() }
        shouldThrow<AssertionError> { "".shouldBeFalsy() }
        shouldThrow<AssertionError> { " ".shouldBeFalsy() }
        shouldThrow<AssertionError> { " ".shouldBeFalsy() }
        shouldThrow<AssertionError> { null.shouldBeFalsy() }
      }

      "should provide error message" {
        shouldThrow<AssertionError> { "yes".shouldBeFalsy() }
            .message.shouldBe("yes should be equal ignoring case one of values: [false, no, n, 0]")
        shouldThrow<AssertionError> { "FALSE" shouldNot beFalsy() }
            .message.shouldBe("FALSE should not be equal ignoring case one of values: [false, no, n, 0]")
      }
    }

    "Should be UUID" - {
      "Should pass for Java generated UUIDs" {
        Gen.uuid().assertAll { uuid ->
          uuid.toString().shouldBeUUID()
          uuid.toString().toUpperCase().shouldBeUUID()
          uuid.toString().toLowerCase().shouldBeUUID()
          shouldThrow<AssertionError> { uuid.toString().shouldNotBeUUID() }
        }
      }

      "Should pass for nil UUID" {
        "00000000-0000-0000-0000-000000000000".shouldBeUUID()
        shouldThrow<AssertionError> { "00000000-0000-0000-0000-000000000000".shouldNotBeUUID() }
      }

      "Should fail for nil UUID if it should be considered invalid" {
        shouldThrow<AssertionError> { "00000000-0000-0000-0000-000000000000".shouldBeUUID(considerNilValid = false) }
        "00000000-0000-0000-0000-000000000000".shouldNotBeUUID(considerNilValid = false)
      }

      "Should fail for strings" {
        Gen.string(31, 41).assertAll(iterations = 10_000) { str ->
          shouldThrow<AssertionError> { str.shouldBeUUID() }
          str.shouldNotBeUUID()
        }
      }

      "Should fail for UUIDs without hyphens (not in accordance with specification)" {
        Gen.uuid().assertAll { uuid ->
          val nonHyphens = uuid.toString().replace("-", "")
          nonHyphens.shouldNotBeUUID()
          shouldThrow<AssertionError> { nonHyphens.shouldBeUUID() }
        }
      }
    }
  }
}
