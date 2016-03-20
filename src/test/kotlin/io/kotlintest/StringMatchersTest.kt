package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.matchers.end
import io.kotlintest.matchers.have
import io.kotlintest.matchers.start
import io.kotlintest.specs.FreeSpec

class StringMatchersTest : FreeSpec(), Matchers {
  init {
    "Matchers should start with x" - {
      "should compare prefix of string" with {
        "bibble" should start with ""
        "bibble" should start with "bib"
        "bibble" should start with "bibble"
      }
      "should fail if string does not start with x" with {
        val t = try {
          "bibble" should start with "vv"
          true
        } catch(e: RuntimeException) {
          false
        }
        t shouldBe false
      }
    }
    "Matchers should end with x" - {
      "should compare prefix of string" with {
        "bibble" should end with ""
        "bibble" should end with "ble"
        "bibble" should end with "bibble"
      }
      "should fail if string does not end with x" with {
        val t = try {
          "bibble" should end with "qwe"
          true
        } catch(e: Exception) {
          false
        }
        t shouldBe false
      }
    }
    "Matchers should have substring x" - {
      "should test string contains substring" with {
        "bibble" should have substring ""
        "bibble" should have substring "bb"
        "bibble" should have substring "bibble"
      }
      "should fail if string does not contains substring" with {
        val t = try {
          "bibble" should have substring "qweqwe"
          true
        } catch(e: Exception) {
          false
        }
        t shouldBe false
      }
    }
  }
}