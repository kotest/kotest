package com.sksamuel.kotlintest.tests.matchers

import io.kotlintest.matchers.endWith
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.include
import io.kotlintest.matchers.match
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldNot
import io.kotlintest.matchers.startWith
import io.kotlintest.matchers.string.beBlank
import io.kotlintest.matchers.string.beEmpty
import io.kotlintest.matchers.string.beLowerCase
import io.kotlintest.matchers.string.beUpperCase
import io.kotlintest.matchers.string.containADigit
import io.kotlintest.matchers.string.containOnlyDigits
import io.kotlintest.matchers.string.haveSameLengthAs
import io.kotlintest.specs.FreeSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.ComparisonFailure

class StringMatchersTest : FreeSpec() {
  init {

    "string shouldBe other" - {
      "should show divergence in error message" {
        shouldThrow<ComparisonFailure> {
          "la tour eiffel" shouldBe "la tour tower london"
        }.message shouldBe "expected:<la tour [tower london]> but was:<la tour [eiffel]>"
      }
    }

    "string should contain" - {
      "should test that a string contains substring" {
        "hello" should include("h")
        "hello" should include("o")
        "hello" should include("ell")
        "hello" should include("hello")
        "hello" should include("")
        shouldThrow<AssertionError> {
          "hello" should include("allo")
        }
      }
    }

    "string should beEmpty()" - {
      "should test that a string has length 0" {
        "" should beEmpty()
        "hello" shouldNot beEmpty()
      }
    }

    "string should containADigit()" - {
      "should test that a string has at least one number" {
        "" shouldNot containADigit()
        "1" should containADigit()
        "a1" should containADigit()
        "a1b" should containADigit()
        "hello" shouldNot containADigit()
      }
    }


    "string should beUpperCase()" - {
      "should test that a string is upper case" {
        "" should beUpperCase()
        "HELLO" should beUpperCase()
        "heLLO" shouldNot beUpperCase()
        "hello" shouldNot beUpperCase()
      }
    }


    "string should beLowerCase()" - {
      "should test that a string is lower case" {
        "" should beLowerCase()
        "hello" should beLowerCase()
        "HELLO" shouldNot beLowerCase()
        "HELlo" shouldNot beLowerCase()
      }
    }


    "string should beBlank()" - {
      "should test that a string has only whitespace" {
        "" should beBlank()
        "     \t     " should beBlank()
        "hello" shouldNot beBlank()
      }
    }

    "string should haveSameLengthAs(other)" - {
      "should test that a string has the same length as another string" {
        "hello" should haveSameLengthAs("world")
        "hello" shouldNot haveSameLengthAs("o")
        "" should haveSameLengthAs("")
        "" shouldNot haveSameLengthAs("o")
        "5" shouldNot haveSameLengthAs("")
      }
    }

    "string should containIgnoringCase(other)" - {
      "should test that a string has the same length as another string" {
        "hello" should haveSameLengthAs("world")
        "hello" shouldNot haveSameLengthAs("o")
        "" should haveSameLengthAs("")
        "" shouldNot haveSameLengthAs("o")
        "5" shouldNot haveSameLengthAs("")
      }
    }

    "should containOnlyDigits()" - {
      "should test that a string only contains 0-9" {
        "hello" shouldNot containOnlyDigits()
        "123123" should containOnlyDigits()
        "" should containOnlyDigits()
        "aa123" shouldNot containOnlyDigits()
      }
    }

    "should endWith" - {
      "should test strings" {
        "hello" should endWith("o")
        "hello" should endWith("")
        "" should endWith("")
        shouldThrow<AssertionError> {
          "" should endWith("h")
        }
        shouldThrow<AssertionError> {
          "hello" should endWith("goodbye")
        }
      }
    }

    "should startWith" - {
      "should test strings" {
        "hello" should startWith("h")
        "hello" should startWith("")
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
        }.message shouldBe "String la tour eiffel should start with la tour tower london (diverged at index 8)"
      }
    }

    "Matchers should start with x" - {
      "should compare prefix of string" {
        "bibble" should startWith("")
        "bibble" should startWith("bib")
        "bibble" should startWith("bibble")
      }
      "should fail if string does not start with x" {
        val t = try {
          "bibble" should startWith("vv")
          true
        } catch (e: AssertionError) {
          false
        }
        t shouldBe false
      }
    }
    "should haveLength(5)" - {
      "should compare length of string" {
        "bibble" should haveLength(6)
        "" should haveLength(0)
        shouldThrow<AssertionError> {
          "" should haveLength(3)
        }
      }
    }
    "Matchers should end with x" - {
      "should compare prefix of string" {
        "bibble" should endWith("")
        "bibble" should endWith("ble")
        "bibble" should endWith("bibble")
      }
      "should fail if string does not end with x" {
        val t = try {
          "bibble" should endWith("qwe")
          true
        } catch (e: AssertionError) {
          false
        }
        t shouldBe false
      }
    }
    "Matchers should have substring x" - {
      "should test string contains substring" {
        "bibble" should include("")
        "bibble" should include("bb")
        "bibble" should include("bibble")
      }
      "should fail if string does not contains substring" {
        val t = try {
          "bibble" should include("qweqwe")
          true
        } catch (e: AssertionError) {
          false
        }
        t shouldBe false
      }
    }
    "String should match regex" - {
      "should test string matches regular expression" {
        "sam" should match("sam")
        "bibble" should match("bibb..")
        "foo" should match(".*")
      }
    }
  }
}
