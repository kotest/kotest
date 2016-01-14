package com.sksamuel.ktest

import kotlin.collections.listOf
import com.sksamuel.ktest.Matchers.*

class MatchersTest : FreeSpec(), Matchers {
  init {
    "Matchers.shouldBe" - {
      "should compare equality" with {
        "a" shouldBe "a"
      }
    }
    "Matchers.shouldEqual" - {
      "should compare equality" with {
        "a" shouldEqual "a"
      }
    }
    "Matcher .should have size x" - {
      "should compare sizes of iterables" with {
        listOf(1, 2, 3) should have size 3
      }
    }
    "Matchers should start with x" - {
      "should compare prefix of string" with {
        "bibble" should start with ""
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