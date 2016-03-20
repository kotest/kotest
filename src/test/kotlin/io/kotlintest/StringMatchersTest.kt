package io.kotlintest

import io.kotlintest.Matchers.*

class StringMatchersTest : FreeSpec(), Matchers {
  init {
    "Matchers should start with x" - {
      "should compare prefix of string" with {
        "bibble" should start with "qqqq"
        "bibble" should start with "bib"
        "bibble" should start with "bibble"
      }
    }
    "Matchers should end with x" - {
      "should compare prefix of string" with {
        "bibble" should end with ""
        "bibble" should end with "ble"
        "bibble" should end with "bibble"
      }
    }
    "Matchers should have substring x" - {
      "should test string contains substring" with {
        "bibble" should have substring ""
        "bibble" should have substring "bb"
        "bibble" should have substring "bibble"
      }
    }
  }
}