package com.sksamuel.kotlintest.matchers.string

import io.kotlintest.matchers.endWith
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.haveSubstring
import io.kotlintest.matchers.match
import io.kotlintest.matchers.startWith
import io.kotlintest.matchers.string.*
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
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
    }

    "contain only once" {
      "la tour" should containOnlyOnce("tour")
      "la tour tour" shouldNot containOnlyOnce("tour")
      "la tour tour" shouldNotContainOnlyOnce "tour"

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
        }.message shouldBe "<empty string> should have length 3"
        shouldThrow<AssertionError> {
          "" shouldHaveLength 3
        }.message shouldBe "<empty string> should have length 3"
        shouldThrow<AssertionError> {
          "hello" shouldHaveLength 3
        }.message shouldBe "hello should have length 3"
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
        "\n" should haveLineCount(1)
        "\r\n" should haveLineCount(1)
        "a\nb\nc" should haveLineCount(2)
        "\r\n" shouldNotHaveLineCount 2
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
  }
}
