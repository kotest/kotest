package com.sksamuel.kotlintest.tests.matchers.string

import io.kotlintest.matchers.endWith
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.match
import io.kotlintest.matchers.startWith
import io.kotlintest.matchers.string.beBlank
import io.kotlintest.matchers.string.beEmpty
import io.kotlintest.matchers.string.beLowerCase
import io.kotlintest.matchers.string.beUpperCase
import io.kotlintest.matchers.string.contain
import io.kotlintest.matchers.string.containADigit
import io.kotlintest.matchers.string.containIgnoringCase
import io.kotlintest.matchers.string.containOnlyDigits
import io.kotlintest.matchers.string.containOnlyOnce
import io.kotlintest.matchers.string.haveSameLengthAs
import io.kotlintest.matchers.string.include
import io.kotlintest.matchers.string.shouldBeBlank
import io.kotlintest.matchers.string.shouldBeEmpty
import io.kotlintest.matchers.string.shouldBeLowerCase
import io.kotlintest.matchers.string.shouldBeUpperCase
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldContainADigit
import io.kotlintest.matchers.string.shouldContainIgnoringCase
import io.kotlintest.matchers.string.shouldContainOnlyDigits
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.matchers.string.shouldHaveSameLengthAs
import io.kotlintest.matchers.string.shouldInclude
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.string.shouldNotBeEmpty
import io.kotlintest.matchers.string.shouldNotBeLowerCase
import io.kotlintest.matchers.string.shouldNotBeUpperCase
import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.matchers.string.shouldNotContainADigit
import io.kotlintest.matchers.string.shouldNotContainIgnoringCase
import io.kotlintest.matchers.string.shouldNotContainOnlyDigits
import io.kotlintest.matchers.string.shouldNotContainOnlyOnce
import io.kotlintest.matchers.string.shouldNotEndWith
import io.kotlintest.matchers.string.shouldNotHaveLength
import io.kotlintest.matchers.string.shouldNotHaveSameLengthAs
import io.kotlintest.matchers.string.shouldNotMatch
import io.kotlintest.matchers.string.shouldNotStartWith
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec

class StringMatchersTest : FreeSpec() {
  init {

    "string shouldBe other" - {
      "should show divergence in error message" {
        shouldThrow<AssertionError> {
          "la tour eiffel" shouldBe "la tour tower london"
        }.message shouldBe "expected:<la tour [tower london]> but was:<la tour [eiffel]>"
      }
    }

    "contain only once" {
      "la tour" should containOnlyOnce("tour")
      "la tour".shouldContain("tour")
      "la tour tour" shouldNot containOnlyOnce("tour")
      "la tour tour".shouldNotContainOnlyOnce("tour")

      shouldThrow<AssertionError> {
        "la tour".shouldContain("wibble")
      }.message shouldBe "String la tour should include substring wibble"
    }

    "contain(regex)" {
      "la tour" should contain("^.*?tour$".toRegex())
      "la tour" shouldNot contain(".*?abc.*?".toRegex())

      "la tour".shouldContain("^.*?tour$".toRegex())
      "la tour".shouldNotContain(".*?abc.*?".toRegex())

      shouldThrow<AssertionError> {
        "la tour".shouldContain(".*?abc.*?".toRegex())
      }.message shouldBe "String la tour should contain regex .*?abc.*?"

      shouldThrow<AssertionError> {
        "la tour".shouldNotContain("^.*?tour$".toRegex())
      }.message shouldBe "String la tour should not contain regex ^.*?tour\$"
    }

    "string should contain" - {
      "should test that a string contains substring" {
        "hello" should include("h")
        "hello".shouldInclude("o")
        "hello" should include("ell")
        "hello" should include("hello")
        "hello" should include("")

        shouldThrow<AssertionError> {
          "hello" should include("allo")
        }.message shouldBe "String hello should include substring allo"

        shouldThrow<AssertionError> {
          "hello".shouldInclude("qwe")
        }.message shouldBe "String hello should include substring qwe"
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
        }.message shouldBe "String hello should be empty"

        shouldThrow<AssertionError> {
          "".shouldNotBeEmpty()
        }.message shouldBe "String  should not be empty"

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
        }.message shouldBe "String hello should contain at least one digits"
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
    }


    "string should beBlank()" - {
      "should test that a string has only whitespace" {
        "" should beBlank()
        "     \t     " should beBlank()
        "hello" shouldNot beBlank()

        "hello".shouldNotBeBlank()
        "   ".shouldBeBlank()
      }
    }

    "string should haveSameLengthAs(other)" {
      "should test that a string has the same length as another string" {
        "hello" should haveSameLengthAs("world")
        "hello" shouldNot haveSameLengthAs("o")
        "" should haveSameLengthAs("")
        "" shouldNot haveSameLengthAs("o")
        "5" shouldNot haveSameLengthAs("")

        "".shouldHaveSameLengthAs("")
        "qwe".shouldHaveSameLengthAs("sdf")
        "".shouldNotHaveSameLengthAs("qweqweqe")
        "qe".shouldNotHaveSameLengthAs("")
        "qe".shouldNotHaveSameLengthAs("fffff")
      }
    }

    "string should containIgnoringCase(other)" {
      "should test that a string contains another string ignoring case" {
        "hello" should containIgnoringCase("HELLO")
        "hello" shouldNot containIgnoringCase("hella")

        "hello".shouldContainIgnoringCase("HEllO")
        "hello".shouldNotContainIgnoringCase("hella")
      }
    }

    "should containOnlyDigits()" {
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

    "should endWith" {
      "should test strings" {
        "hello" should endWith("o")
        "hello" should endWith("")
        "hello".shouldEndWith("")
        "hello".shouldEndWith("lo")
        "hello".shouldEndWith("o")
        "hello".shouldNotEndWith("w")
        "" should endWith("")
        shouldThrow<AssertionError> {
          "" should endWith("h")
        }
        shouldThrow<AssertionError> {
          "hello" should endWith("goodbye")
        }
      }
    }

    "should startWith" {
      "should test strings" {
        "hello" should startWith("h")
        "hello" should startWith("")
        "hello".shouldStartWith("")
        "hello".shouldStartWith("h")
        "hello".shouldStartWith("he")
        "hello".shouldNotStartWith("w")
        "hello".shouldNotStartWith("wo")
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

    "should haveLength(5)" - {
      "should compare length of string" {
        "bibble" should haveLength(6)
        "" should haveLength(0)
        "".shouldHaveLength(0)
        "hello".shouldNotHaveLength(3)
        "hello".shouldHaveLength(5)
        shouldThrow<AssertionError> {
          "" should haveLength(3)
        }.message shouldBe "String  should have length 3"
        shouldThrow<AssertionError> {
          "".shouldHaveLength(3)
        }.message shouldBe "String  should have length 3"
        shouldThrow<AssertionError> {
          "hello".shouldHaveLength(3)
        }.message shouldBe "String hello should have length 3"
        shouldThrow<AssertionError> {
          "hello".shouldNotHaveLength(5)
        }.message shouldBe "String hello should not have length 5"
      }
    }

    "Matchers should end with x" - {
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
        "foo".shouldMatch(".*")
        "foo".shouldMatch("foo")
        "foo".shouldMatch("f..")
        "boo".shouldNotMatch("foo")
        "boo".shouldNotMatch("f..")


      }
    }
  }
}
