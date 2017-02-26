package io.kotlintest.matchers

import io.kotlintest.specs.FreeSpec

class StringMatchersTest : FreeSpec() {
  init {

    "string shouldBe other" - {
      "should show divergence in error message" {
        shouldThrow<AssertionError> {
          "la tour eiffel" shouldBe "la tour tower london"
        }.message shouldBe "String la tour eiffel should be equal to la tour tower london (diverged at index 8)"
      }
    }

    "should contain" - {
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

    "should endWith" - {
      "should test strings" {
        "hello" should endWith("o")
        "hello" should startWith("")
        "" should startWith("")
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
        } catch(e: AssertionError) {
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
        } catch(e: AssertionError) {
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
        } catch(e: AssertionError) {
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